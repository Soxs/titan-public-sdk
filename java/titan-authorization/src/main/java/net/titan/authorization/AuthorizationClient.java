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
    private final String pluginId;
    private final Map<Long,Pending> pending=new LinkedHashMap<>();
    private final Queue<Runnable> completions=new ConcurrentLinkedQueue<>();
    private AuthorizationBackend backend;
    private Map<String,Object> context;
    private Runnable pulseHook=()->{};
    private java.util.function.IntConsumer tickHook=tick->{};
    private java.util.function.Consumer<String> stopHook=reason->{};
    AuthorizationClient(Plugin owner){this(owner,new ReceiptVerifier());}
    AuthorizationClient(Plugin owner,ReceiptVerifier verifier){
        this.owner=Objects.requireNonNull(owner);this.verifier=verifier;
        PluginDescriptor descriptor=owner.getClass().getAnnotation(PluginDescriptor.class);
        pluginId=descriptor==null?"":descriptor.id();
    }
    synchronized void hooks(Runnable pulse,java.util.function.IntConsumer tick,java.util.function.Consumer<String> stop){pulseHook=pulse;tickHook=tick;stopHook=stop;}
    // Background task completion must not invoke plugin/game APIs off the owner thread.
    void dispatch(Runnable completion){completions.add(completion);}
    static String nonce(){byte[] bytes=new byte[32];new SecureRandom().nextBytes(bytes);return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);}
    private boolean bind(){
        AuthorizationBackend candidate=TitanRuntime.getAuthorizationBackend();
        if(candidate==null||!candidate.register(owner,this))return false;
        String raw=candidate.context(owner);if(raw==null||raw.isEmpty())return false;
        Map<String,Object> next=Json.object(raw);
        if(pluginId.isEmpty()||!pluginId.equals(next.get("runtime_plugin_id")))throw new IllegalArgumentException("wrong_plugin");
        boolean changed=context!=null&&!context.equals(next);
        backend=candidate;context=next;
        if(changed){cancelPending("session_changed");stopHook.accept("session_changed");}
        return true;
    }
    synchronized CompletableFuture<ReceiptVerifier.Receipt> request(String operation,Map<String,Object> payload,String runId){
        CompletableFuture<ReceiptVerifier.Receipt> future=new CompletableFuture<>();
        try {
            if(!bind())throw new IllegalStateException("authorization_unavailable");
            if(pending.size()>=16)throw new IllegalStateException("too_many_requests");
            String request=nonce(),challenge=nonce();long started=System.nanoTime();
            long id=backend.submit(owner,operation,Json.write(Json.map("request_id",request,"challenge",challenge,"payload",payload)));
            if(id==0)throw new IllegalStateException("authorization_unavailable");
            pending.put(id,new Pending(operation,request,challenge,runId,started,context,future));
        }catch(Throwable failure){future.completeExceptionally(failure);}
        return future;
    }
    synchronized void cancelPending(String reason){
        List<Map.Entry<Long,Pending>> copy=new ArrayList<>(pending.entrySet());pending.clear();
        for(Map.Entry<Long,Pending> entry:copy){if(backend!=null)backend.cancel(owner,entry.getKey());entry.getValue().future.completeExceptionally(new CancellationException(reason));}
    }
    synchronized void stopTransport(){if(backend!=null)backend.authorizationStop(owner);}
    synchronized void deadline(long remainingMillis){try{if(bind())backend.deadline(owner,Math.max(1,remainingMillis));}catch(Throwable ignored){}}
    synchronized void close(){cancelPending("closed");if(backend!=null)backend.unregister(owner,this);}
    @Override public synchronized void pulse(){
        for(Runnable completion;(completion=completions.poll())!=null;)completion.run();
        for(Map.Entry<Long,Pending> entry:new ArrayList<>(pending.entrySet())){
            Pending p=entry.getValue();String response=backend.poll(owner,entry.getKey());
            if(response==null||response.isEmpty()){
                if(System.nanoTime()-p.started<(p.operation.equals("report")?5000000000L:30000000000L))continue;
                pending.remove(entry.getKey());backend.cancel(owner,entry.getKey());p.future.completeExceptionally(new TimeoutException("authorization_timeout"));continue;
            }
            pending.remove(entry.getKey());
            try{p.future.complete(verifier.verify(response,p.operation,p.request,p.challenge,p.context,p.runId,p.started));}
            catch(Throwable failure){p.future.completeExceptionally(failure);}
        }
        pulseHook.run();
    }
    @Override public void tick(int tick){pulse();tickHook.accept(tick);}
    @Override public synchronized void stopped(String reason){cancelPending(reason);stopHook.accept(reason);}
    private static final class Pending {
        final String operation,request,challenge,runId;final long started;final Map<String,Object> context;
        final CompletableFuture<ReceiptVerifier.Receipt> future;
        Pending(String o,String r,String c,String run,long time,Map<String,Object> ctx,CompletableFuture<ReceiptVerifier.Receipt> f){operation=o;request=r;challenge=c;runId=run;started=time;context=ctx;future=f;}
    }
}
