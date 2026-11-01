package net.titan.authorization;

import net.titan.api.internal.AuthorizationBackend;
import net.titan.api.internal.TitanRuntime;
import net.titan.api.plugins.Plugin;
import net.titan.api.plugins.PluginDescriptor;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;

/** Plugin-local bounded request correlation, never a network call on the game thread. */
final class AuthorizationClient implements AuthorizationBackend.Listener {
    private final Plugin owner;
    private final ReceiptVerifier verifier;
    private final java.util.function.LongSupplier monotonicNanos;
    private final String pluginId;
    private final Map<Long,Pending> pending=new LinkedHashMap<>();
    private final Queue<Runnable> completions=new ConcurrentLinkedQueue<>();
    private final Map<String,Terminal> terminalOutbox=new LinkedHashMap<>();
    private ScheduledExecutorService terminalExecutor;
    private AuthorizationBackend backend;
    private Map<String,Object> context;
    private boolean identityInvalidated;
    private volatile boolean ownerStopped;
    private Runnable pulseHook=()->{};
    private java.util.function.IntConsumer tickHook=tick->{};
    private java.util.function.Consumer<String> stopHook=reason->{};
    AuthorizationClient(Plugin owner){this(owner,new ReceiptVerifier());}
    AuthorizationClient(Plugin owner,ReceiptVerifier verifier){
        this(owner,verifier,System::nanoTime);
    }
    AuthorizationClient(Plugin owner,ReceiptVerifier verifier,java.util.function.LongSupplier monotonic){
        this.owner=Objects.requireNonNull(owner);this.verifier=verifier;
        monotonicNanos=monotonic;
        PluginDescriptor descriptor=owner.getClass().getAnnotation(PluginDescriptor.class);
        pluginId=descriptor==null?"":descriptor.id();
    }
    synchronized void hooks(Runnable pulse,java.util.function.IntConsumer tick,java.util.function.Consumer<String> stop){pulseHook=pulse;tickHook=tick;stopHook=stop;}
    // Background task completion must not invoke plugin/game APIs off the owner thread.
    void dispatch(Runnable completion){completions.add(completion);if(ownerStopped)drainCompletions();}
    private void drainCompletions(){for(Runnable completion;(completion=completions.poll())!=null;)completion.run();}
    static String nonce(){byte[] bytes=new byte[32];new SecureRandom().nextBytes(bytes);return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);}
    private boolean bind(){return bind(false);}
    private boolean bind(boolean terminal){
        AuthorizationBackend candidate=TitanRuntime.getAuthorizationBackend();
        // Terminal requests use the exact cached owner channel after disable/unload.
        // They must not re-register an unloaded plugin as a callback root.
        if(candidate==null||!terminal&&!candidate.register(owner,this))return false;
        if(!terminal)ownerStopped=false;
        String raw=candidate.context(owner);if(raw==null||raw.isEmpty())return false;
        Map<String,Object> next=Json.object(raw);
        if(pluginId.isEmpty()||!pluginId.equals(next.get("runtime_plugin_id")))throw new IllegalArgumentException("wrong_plugin");
        boolean changed=context!=null&&!context.equals(next);
        boolean invalidSession=Boolean.FALSE.equals(next.get("session_valid"));
        backend=candidate;
        // Retirement already fenced callbacks. Terminal transport may run after
        // an owned task settles off-thread, so it must not invoke owner hooks.
        // Keep the ordinary context unchanged so its next owner pulse still
        // observes and fences any identity/session change.
        if(terminal){if(context==null)context=next;return true;}
        context=next;
        if(changed||invalidSession&&!identityInvalidated){
            identityInvalidated=invalidSession;cancelPending("session_changed");stopHook.accept("session_changed");
            if(!terminal)throw new CancellationException("session_changed");
        }
        if(invalidSession&&!terminal)throw new CancellationException("session_changed");
        return true;
    }
    synchronized CompletableFuture<ReceiptVerifier.Receipt> request(String operation,Map<String,Object> payload,String runId){
        CompletableFuture<ReceiptVerifier.Receipt> future=new CompletableFuture<>();
        try {
            if(!bind(operation.equals("cancel")))throw new IllegalStateException("authorization_unavailable");
            if(pending.size()>=16)throw new IllegalStateException("too_many_requests");
            String request=nonce(),challenge=nonce();long started=monotonicNanos.getAsLong();
            long id=backend.submit(owner,operation,Json.write(Json.map("request_id",request,"challenge",challenge,"payload",payload)));
            if(id==0)throw new IllegalStateException("authorization_unavailable");
            pending.put(id,new Pending(operation,request,challenge,runId,started,context,future));
        }catch(Throwable failure){future.completeExceptionally(failure);}
        return future;
    }
    synchronized void cancelPending(String reason){
        cancelPending(reason,false);
    }
    synchronized void cancelPending(String reason,boolean keepTerminal){
        for(Map.Entry<Long,Pending> entry:new ArrayList<>(pending.entrySet())){
            boolean terminal=entry.getValue().operation.equals("cancel");if(keepTerminal&&terminal)continue;
            pending.remove(entry.getKey());
            // Terminal cancellation must remain in the host queue after SDK unload.
            if(backend!=null&&!terminal)backend.cancel(owner,entry.getKey());
            entry.getValue().future.completeExceptionally(new CancellationException(reason));
        }
    }
    void stopTransport(){synchronized(this){if(backend!=null)backend.authorizationStop(owner);ownerStopped=true;}drainCompletions();}
    synchronized boolean sessionValid(){return !identityInvalidated;}
    synchronized void deadline(long remainingMillis){try{if(bind())backend.deadline(owner,Math.max(1,remainingMillis));}catch(Throwable ignored){}}
    synchronized void close(){cancelPending("closed",true);if(backend!=null)backend.unregister(owner,this);}
    void completeRun(Map<String,Object> payload,String runId,java.util.function.Consumer<ReceiptVerifier.Receipt> receipt){
        synchronized(this){
            terminalOutbox.putIfAbsent(runId,new Terminal(payload,runId,receipt));
            if(terminalExecutor==null){
                terminalExecutor=Executors.newSingleThreadScheduledExecutor(r->{Thread thread=new Thread(r,"titan-plugin-retirement");thread.setDaemon(true);return thread;});
                terminalExecutor.scheduleWithFixedDelay(()->{try{retryTerminal();pollPending(true);}catch(Throwable ignored){}},250,250,TimeUnit.MILLISECONDS);
            }
        }
        retryTerminal();
    }
    private void retryTerminal(){
        List<Terminal> ready=new ArrayList<>();
        synchronized(this){
            long now=monotonicNanos.getAsLong(),realNow=System.nanoTime();
            if(backend!=null&&TitanRuntime.getAuthorizationBackend()!=backend)terminalOutbox.clear();
            terminalOutbox.values().removeIf(item->now>=item.expires||realNow>=item.realExpires);
            for(Terminal item:terminalOutbox.values())if(!item.pending&&now>=item.next){item.pending=true;ready.add(item);}
            if(terminalOutbox.isEmpty()&&terminalExecutor!=null){terminalExecutor.shutdown();terminalExecutor=null;}
        }
        for(Terminal item:ready)request("cancel",item.payload,item.runId).whenComplete((receipt,error)->{
            boolean released=error==null&&item.runId.equals(receipt.result.get("run_id"))&&Boolean.TRUE.equals(receipt.result.get("released"))&&Long.valueOf(0).equals(receipt.result.get("held_until"));
            synchronized(this){
                if(terminalOutbox.get(item.runId)!=item)return;
                if(released)terminalOutbox.remove(item.runId);
                else{item.pending=false;item.next=monotonicNanos.getAsLong()+1000000000L;}
            }
            if(error==null)item.receipt.accept(receipt);
        });
    }
    @Override public void pulse(){
        synchronized(this){try{bind();}catch(Throwable ignored){}} // Identity invalidation is independent of requests.
        drainCompletions();
        retryTerminal();pollPending(false);pulseHook.run();
    }
    private void pollPending(boolean terminalOnly){
        List<Map.Entry<Long,Pending>> copy;
        synchronized(this){copy=new ArrayList<>(pending.entrySet());}
        for(Map.Entry<Long,Pending> entry:copy){
            Pending p=entry.getValue();String response=null;Throwable failure=null;
            synchronized(this){
                if(pending.get(entry.getKey())!=p||terminalOnly&&!p.operation.equals("cancel"))continue;
                try{response=backend.poll(owner,entry.getKey());}catch(Throwable error){failure=error;}
                if(failure==null&&(response==null||response.isEmpty())){
                    if(monotonicNanos.getAsLong()-p.started<(p.operation.equals("report")?5000000000L:p.operation.equals("cancel")?330000000000L:30000000000L))continue;
                    backend.cancel(owner,entry.getKey());failure=new TimeoutException("authorization_timeout");
                }
                pending.remove(entry.getKey());
            }
            if(failure!=null){p.future.completeExceptionally(failure);continue;}
            try{
                if(Json.object(response).get("error") instanceof String)throw new IllegalStateException("authorization_transport_unavailable");
                p.future.complete(verifier.verify(response,p.operation,p.request,p.challenge,p.context,p.runId,p.started));
            }
            catch(Throwable error){p.future.completeExceptionally(error);}
        }
    }
    @Override public void tick(int tick){pulse();tickHook.accept(tick);}
    @Override public void stopped(String reason){cancelPending(reason);stopHook.accept(reason);ownerStopped=true;drainCompletions();}
    private static final class Pending {
        final String operation,request,challenge,runId;final long started;final Map<String,Object> context;
        final CompletableFuture<ReceiptVerifier.Receipt> future;
        Pending(String o,String r,String c,String run,long time,Map<String,Object> ctx,CompletableFuture<ReceiptVerifier.Receipt> f){operation=o;request=r;challenge=c;runId=run;started=time;context=ctx;future=f;}
    }
    private final class Terminal {
        final Map<String,Object> payload;
        final String runId;
        final java.util.function.Consumer<ReceiptVerifier.Receipt> receipt;
        final long expires=monotonicNanos.getAsLong()+330000000000L,realExpires=System.nanoTime()+330000000000L;
        long next;boolean pending;
        Terminal(Map<String,Object> payload,String runId,java.util.function.Consumer<ReceiptVerifier.Receipt> receipt){this.payload=new LinkedHashMap<>(payload);this.runId=runId;this.receipt=receipt;}
    }
}
