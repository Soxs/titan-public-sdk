package net.titan.authorization;

import net.titan.api.plugins.Plugin;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/** Verified access for the current customer, scoped by the calling plugin. */
public final class Entitlements implements AutoCloseable {
    public enum Decision { GRANTED, DENIED, UNKNOWN, SCOPE_DENIED }
    public enum Freshness { CURRENT, STALE, UNAVAILABLE }
    public static final class Usage {
        public final long active,retiring,observedAt;
        private Usage(Map<String,Object> value){active=integer(value,"active",0);retiring=integer(value,"retiring",0);observedAt=integer(value,"observed_at",0);}
    }
    public static final class Entry {
        public final String key,pluginId,canonicalPluginId,runtimePluginId,displayName,accessMode;
        public final boolean owned,entitled,runtimeEligible,deliveryEligible;
        public final Long accessExpiresAt,runtimeAccessExpiresAt,includedInstances,totalInstances,nextChangeAt;
        public final long extraInstances;
        public final Usage usage;
        public final List<String> source;
        public final List<ExtraExpiry> extraExpiries;
        public final Object value;
        private Entry(Map<String,Object> row){
            key=Json.string(row,"key");pluginId=text(row,"plugin_id");canonicalPluginId=text(row,"canonical_plugin_id");
            runtimePluginId=text(row,"runtime_plugin_id");
            displayName=text(row,"display_name");accessMode=text(row,"access_mode");owned=Boolean.TRUE.equals(row.get("owned"));
            entitled=Boolean.TRUE.equals(row.get("entitled"));runtimeEligible=Boolean.TRUE.equals(row.get("runtime_eligible"));deliveryEligible=Boolean.TRUE.equals(row.get("delivery_eligible"));
            accessExpiresAt=nullableInteger(row,"access_expires_at");includedInstances=nullableInteger(row,"included_instances");totalInstances=nullableInteger(row,"total_instances");nextChangeAt=nullableInteger(row,"next_change_at");
            runtimeAccessExpiresAt=nullableInteger(row,"runtime_access_expires_at");
            extraInstances=integer(row,"extra_instances",0);usage=new Usage(row.get("usage") instanceof Map?Json.object(row.get("usage")):Collections.emptyMap());
            List<String> sources=new ArrayList<>();if(row.get("source") instanceof List)for(Object item:(List<?>)row.get("source")){if(!(item instanceof String))throw new IllegalArgumentException();sources.add((String)item);}
            source=Collections.unmodifiableList(sources);value=row.get("value");
            List<ExtraExpiry> extras=new ArrayList<>();if(row.get("extra_expiries") instanceof List)for(Object item:(List<?>)row.get("extra_expiries"))extras.add(new ExtraExpiry(Json.object(item)));
            extraExpiries=Collections.unmodifiableList(extras);
        }
    }
    public static final class ExtraExpiry {
        public final long quantity;public final Long expiresAt;
        private ExtraExpiry(Map<String,Object> row){quantity=Json.number(row,"quantity");expiresAt=nullableInteger(row,"expires_at");}
    }
    private static String text(Map<String,Object> row,String key){return row.get(key) instanceof String?(String)row.get(key):"";}
    private static Long nullableInteger(Map<String,Object> row,String key){return row.get(key)==null?null:Json.number(row,key);}
    private static long integer(Map<String,Object> row,String key,long fallback){return row.get(key)==null?fallback:Json.number(row,key);}
    public static final class Check {
        public final Decision decision;
        public final String reason;
        public final Entry entitlement;
        public final long validUntil,verifiedAt;
        private Check(Decision d,String r,Entry e,long until){decision=d;reason=r;entitlement=e;validUntil=until;verifiedAt=until==0?0:System.currentTimeMillis()/1000;}
    }
    public static final class Snapshot {
        public final List<Entry> entries;
        public final Map<String,Object> publicClient;
        public final String snapshotId;
        public final long issuedAt,validUntil;
        public final boolean complete;
        private final long deadline;
        private Snapshot(List<Entry> entries,Map<String,Object> publicClient,String id,long issued,long until,long deadline,boolean complete){
            this.entries=Collections.unmodifiableList(new ArrayList<>(entries));this.publicClient=Collections.unmodifiableMap(new LinkedHashMap<>(publicClient));
            snapshotId=id;issuedAt=issued;validUntil=until;this.deadline=deadline;this.complete=complete;
        }
        public Freshness freshness(){return !complete?Freshness.UNAVAILABLE:System.nanoTime()<deadline?Freshness.CURRENT:Freshness.STALE;}
        public Optional<Entry> get(String key){return entries.stream().filter(entry->key.equals(entry.key)).findFirst();}
    }
    public static Entitlements forPlugin(Plugin owner){return new Entitlements(owner);}
    private final AuthorizationClient client;
    private final List<Consumer<Snapshot>> subscribers=new ArrayList<>();
    private Snapshot snapshot=new Snapshot(Collections.emptyList(),Collections.emptyMap(),"",0,0,0,false);
    private CompletableFuture<Snapshot> listing;
    private long nextRefresh,cacheRevision;
    private Freshness observed=Freshness.UNAVAILABLE;
    private boolean closed;
    private Entitlements(Plugin owner){client=new AuthorizationClient(owner);client.hooks(this::pulse,tick->{},reason->invalidate());}
    public CompletionStage<Check> check(){return check(null);}
    /** An omitted key checks this plugin; other keys must be in its approved scope. */
    public synchronized CompletionStage<Check> check(String key){
        if(closed)return CompletableFuture.completedFuture(new Check(Decision.UNKNOWN,"closed",null,0));
        Map<String,Object> payload=key==null?Collections.emptyMap():Json.map("plugin_id",key);
        return client.request("check",payload,null).handle((receipt,error)->{
            if(error!=null){stale();return new Check(Decision.UNKNOWN,"authorization_unavailable",null,0);}
            try {
            Map<String,Object> result=receipt.result;
            Entry entry=result.get("entitlement") instanceof Map?new Entry(Json.object(result.get("entitlement"))):null;
            String code=result.get("code") instanceof String?(String)result.get("code"):"";
            Decision decision="scope-denied".equals(code)?Decision.SCOPE_DENIED:entry!=null&&entry.entitled?Decision.GRANTED:
                "not-entitled".equals(code)||entry!=null&&!entry.entitled?Decision.DENIED:Decision.UNKNOWN;
            // A new signed denial must not leave an older approval marked current.
            if(!Boolean.TRUE.equals(result.get("allowed"))||decision==Decision.DENIED)invalidate();
            return new Check(decision,code,entry,Json.number(receipt.claims,"exp"));
            }catch(RuntimeException invalid){stale();return new Check(Decision.UNKNOWN,"invalid_authorization_receipt",null,0);}
        });
    }
    /** Coalesces in-flight callers and returns a complete verified scoped snapshot. */
    public synchronized CompletionStage<Snapshot> list(){
        if(closed)return CompletableFuture.completedFuture(snapshot);
        if(listing!=null)return listing;
        listing=new CompletableFuture<>();CompletableFuture<Snapshot> result=listing;
        page(null,new ArrayList<>(),new HashSet<>(),null,-1,Long.MAX_VALUE,0,cacheRevision,result);return result;
    }
    private void page(String cursor,List<Entry> entries,Set<String> keys,String snapshotId,long total,long deadline,int pages,long revision,CompletableFuture<Snapshot> future){
        if(pages>=64){finishList(null,future);return;}
        client.request("list",cursor==null?Collections.emptyMap():Json.map("cursor",cursor),null).whenComplete((receipt,error)->{
            synchronized(Entitlements.this){
                if(error!=null||closed||revision!=cacheRevision){finishList(null,future);return;}
                try{
                    Map<String,Object> result=receipt.result;if(!Boolean.TRUE.equals(result.get("allowed"))){invalidate();throw new IllegalArgumentException();}
                    String id=Json.string(result,"snapshot_id");if(snapshotId!=null&&!snapshotId.equals(id))throw new IllegalArgumentException();
                    Object items=result.get("entries");if(!(items instanceof List))throw new IllegalArgumentException();
                    long count=Json.number(result,"total_entries");
                    if(count<0||count>10000||total>=0&&count!=total||Json.number(result,"offset")!=entries.size())throw new IllegalArgumentException();
                    for(Object item:(List<?>)items){Entry entry=new Entry(Json.object(item));if(!keys.add(entry.key))throw new IllegalArgumentException();entries.add(entry);}
                    if(entries.size()>count)throw new IllegalArgumentException();
                    long until=Math.min(deadline,receipt.deadlineNanos);
                    Object next=result.get("next_cursor");
                    if(!Boolean.TRUE.equals(result.get("complete"))){if(((List<?>)items).isEmpty()||!(next instanceof String)||((String)next).isEmpty()||entries.size()>=count)throw new IllegalArgumentException();page((String)next,entries,keys,id,count,until,pages+1,revision,future);return;}
                    if(entries.size()!=count)throw new IllegalArgumentException();
                    if(next!=null&&!"".equals(next))throw new IllegalArgumentException();
                    Map<String,Object> publicClient=Json.object(result.get("public_client"));
                    finishList(new Snapshot(entries,publicClient,id,Json.number(result,"issued_at"),Json.number(result,"valid_until"),until,true),future);
                }catch(Throwable failure){finishList(null,future);}
            }
        });
    }
    private void finishList(Snapshot value,CompletableFuture<Snapshot> future){
        if(value!=null){snapshot=value;notifyChanged();}else stale();
        listing=null;nextRefresh=System.nanoTime()+60000000000L;future.complete(snapshot);
    }
    public synchronized Snapshot snapshot(){return snapshot;}
    /** Subscription lifetime follows the owner; closing removes this subscription. */
    public synchronized AutoCloseable onChanged(Consumer<Snapshot> listener){
        Objects.requireNonNull(listener);subscribers.add(listener);list();
        return ()->{synchronized(Entitlements.this){subscribers.remove(listener);}};
    }
    private synchronized void pulse(){
        Freshness current=snapshot.freshness();if(current!=observed){observed=current;notifyChanged();}
        if(!subscribers.isEmpty()&&listing==null&&System.nanoTime()>=nextRefresh)list();
    }
    private void notifyChanged(){observed=snapshot.freshness();for(Consumer<Snapshot> listener:new ArrayList<>(subscribers)){try{listener.accept(snapshot);}catch(Throwable ignored){}}}
    private synchronized void stale(){if(snapshot.complete){snapshot=new Snapshot(snapshot.entries,snapshot.publicClient,snapshot.snapshotId,snapshot.issuedAt,snapshot.validUntil,0,true);notifyChanged();}}
    private synchronized void invalidate(){cacheRevision++;snapshot=new Snapshot(Collections.emptyList(),Collections.emptyMap(),"",0,0,0,false);notifyChanged();}
    @Override public synchronized void close(){closed=true;client.close();invalidate();subscribers.clear();}
}
