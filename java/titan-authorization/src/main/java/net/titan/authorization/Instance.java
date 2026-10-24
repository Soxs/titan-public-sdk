package net.titan.authorization;

import net.titan.api.plugins.Plugin;
import net.titan.api.panel.Panel;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Consumer;
import java.util.function.BooleanSupplier;
import java.util.concurrent.atomic.AtomicBoolean;

/** One managed concurrent run, separate from enabling its owning plugin. */
public final class Instance implements AutoCloseable {
    public enum State { STOPPED, STARTING, RUNNING, RECONNECTING, STOPPING, FAILED }
    public enum StopReason { USER, COMPLETED, DENIED, START_FAILED, CALLBACK_FAILED, PERMIT_EXPIRED, DISABLED, UNLOADED, SESSION_CHANGED }
    public interface Bot {
        /** Called on the game thread only after a plugin-verified permit. */
        boolean start() throws Exception;
        default void tick(int tick) throws Exception {}
        /** Must promptly signal owned work to stop; may follow a partial start. */
        void stop(StopReason reason) throws Exception;
    }
    public interface Cancellation { boolean cancelled(); }
    /** Adapt an existing void-start worker with ordinary method references. */
    public static Bot adapt(Runnable start,IntConsumer tick,Consumer<StopReason> stop){
        Objects.requireNonNull(start);return adaptChecked(()->{start.run();return true;},tick,stop);
    }
    /** Variant retaining an existing worker's boolean startup failure result. */
    public static Bot adaptChecked(BooleanSupplier start,IntConsumer tick,Consumer<StopReason> stop){
        Objects.requireNonNull(start);Objects.requireNonNull(tick);Objects.requireNonNull(stop);
        return new Bot(){public boolean start(){return start.getAsBoolean();}public void tick(int value){tick.accept(value);}public void stop(StopReason reason){stop.accept(reason);}};
    }
    public static final class StartResult {
        public final boolean started;
        public final String reason;
        private StartResult(boolean started,String reason){this.started=started;this.reason=reason;}
    }
    private static final Map<Plugin,WeakReference<Instance>> BINDINGS=new WeakHashMap<>();
    public static synchronized Instance bind(Plugin owner,Bot bot){
        Objects.requireNonNull(owner);Objects.requireNonNull(bot);
        WeakReference<Instance> ref=BINDINGS.get(owner);Instance previous=ref==null?null:ref.get();
        if(previous!=null&&!previous.closed){if(previous.bot!=bot)throw new IllegalStateException("One instance binding per plugin");return previous;}
        Instance instance=new Instance(owner,bot);BINDINGS.put(owner,new WeakReference<>(instance));return instance;
    }
    private final Bot bot;
    private final AuthorizationClient client;
    private final java.util.function.LongSupplier monotonicNanos,wallMillis;
    private ScheduledExecutorService watchdogExecutor;
    private ScheduledFuture<?> watchdogTask;
    private final AtomicBoolean expiredSignal=new AtomicBoolean();
    private volatile long watchdogNanos,watchdogWallMillis;
    private State state=State.STOPPED;
    private volatile long epoch,permitDeadline,permitWallMillis;
    private long sequence,permitSequence,startDeadline,nextReport,nextAcquire,retryWallMillis,identityEpoch;
    private int retryAttempt;
    private String lastReason="";
    private long releasePendingUntil;
    private String runId,leaseId;
    private boolean entered,reportPending,closed,finitePending,waitingForSlot;
    private CompletableFuture<StartResult> startFuture;
    private CompletionStage<?> task;
    private Instance(Plugin owner,Bot bot){this(owner,bot,new ReceiptVerifier());}
    Instance(Plugin owner,Bot bot,ReceiptVerifier verifier){
        this(owner,bot,verifier,System::nanoTime,System::currentTimeMillis);
    }
    Instance(Plugin owner,Bot bot,ReceiptVerifier verifier,java.util.function.LongSupplier monotonic,java.util.function.LongSupplier wall){
        this.bot=bot;monotonicNanos=monotonic;wallMillis=wall;
        client=new AuthorizationClient(owner,verifier,monotonic);client.hooks(this::pulse,this::tick,this::runtimeStop);
    }
    private void startWatchdog(){
        stopWatchdog();
        ScheduledExecutorService executor=Executors.newSingleThreadScheduledExecutor(r->{Thread thread=new Thread(r,"titan-plugin-permit");thread.setDaemon(true);return thread;});
        watchdogExecutor=executor;
        WeakReference<Instance> weak=new WeakReference<>(this);
        watchdogTask=executor.scheduleAtFixedRate(()->{Instance run=weak.get();if(run!=null)run.watchdog();else executor.shutdown();},250,250,TimeUnit.MILLISECONDS);
    }
    private void stopWatchdog(){
        if(watchdogTask!=null){watchdogTask.cancel(false);watchdogTask=null;}
        if(watchdogExecutor!=null){watchdogExecutor.shutdownNow();watchdogExecutor=null;}
    }
    private void hostDeadline(){client.deadline(Math.min((watchdogNanos-monotonicNanos.getAsLong())/1000000L,watchdogWallMillis-wallMillis.getAsLong()));}
    private void watchdog(){
        long limit=watchdogNanos,wall=watchdogWallMillis;
        if(limit!=0&&(monotonicNanos.getAsLong()>=limit||wallMillis.getAsLong()>=wall)&&expiredSignal.compareAndSet(false,true))
            client.dispatch(()->{synchronized(this){if(expiredSignal.get())finish(state==State.STARTING?StopReason.START_FAILED:StopReason.PERMIT_EXPIRED,state==State.STARTING?(entered?"startup_timeout":"authorization_timeout"):"permit_expired");}});
    }
    private boolean expired(){return expiredSignal.get()||monotonicNanos.getAsLong()>=permitDeadline||wallMillis.getAsLong()>=permitWallMillis;}
    private boolean running(){return state==State.RUNNING||state==State.RECONNECTING;}
    public synchronized State status(){return state;}
    public synchronized String reason(){
        if(!waitingForSlot)return lastReason;
        long remaining=Math.min((nextAcquire-monotonicNanos.getAsLong())/1000000L,retryWallMillis-wallMillis.getAsLong());
        return "Waiting for an instance slot. Retrying in "+Math.max(0,(remaining+999)/1000)+"s.";
    }
    /** Use control(actionId) from the matching onPanelAction callback. */
    public synchronized void controls(Panel panel){
        panel.label("Status",state.name()).label("Reason",reason()).button("Start",0x415a0001).button("Stop",0x415a0002);
        if(!active()&&releasePendingUntil()!=0)panel.wrapped("Last confirmed reservation: "+Math.max(0,releasePendingUntil()-wallMillis.getAsLong()/1000)+"s remaining");
        if(!active()&&(lastReason.equals("authorization_unavailable")||lastReason.equals("authorization_timeout")))panel.wrapped("Reservation status is unavailable. Start checks capacity online.");
    }
    public boolean control(int actionId){if(actionId==0x415a0001){start();return true;}if(actionId==0x415a0002){stop();return true;}return false;}
    /** Latest verified Unix-seconds reservation horizon; zero means none currently known, not confirmed free capacity. */
    public synchronized long releasePendingUntil(){return releasePendingUntil>wallMillis.getAsLong()/1000?releasePendingUntil:0;}
    public synchronized boolean active(){return running()&&!expired();}
    public synchronized CompletionStage<StartResult> start(){
        if(closed)return CompletableFuture.completedFuture(new StartResult(false,"closed"));
        if(state==State.STOPPING)return CompletableFuture.completedFuture(new StartResult(false,"stopping"));
        if(state==State.STARTING)return startFuture;
        if(active())return CompletableFuture.completedFuture(new StartResult(true,"already_running"));
        if(running())finish(StopReason.PERMIT_EXPIRED,"permit_expired");
        epoch++;state=State.STARTING;entered=false;reportPending=false;retryAttempt=0;lastReason="";expiredSignal.set(false);waitingForSlot=false;
        runId=AuthorizationClient.nonce();leaseId=null;permitSequence=0;
        startFuture=new CompletableFuture<>();CompletableFuture<StartResult> result=startFuture;
        acquire();
        return result;
    }
    private void acquire(){
        final long generation=epoch;
        waitingForSlot=false;startDeadline=monotonicNanos.getAsLong()+30000000000L;expiredSignal.set(false);
        watchdogNanos=startDeadline;watchdogWallMillis=wallMillis.getAsLong()+30000;startWatchdog();
        client.request("acquire",Json.map("run_id",runId,"sequence",++sequence),runId).whenComplete((receipt,error)->{
            synchronized(Instance.this){
                if(epoch!=generation||state!=State.STARTING)return;
                if(error!=null){finish(StopReason.DENIED,"authorization_unavailable");return;}
                try {
                    if(monotonicNanos.getAsLong()>=startDeadline||expiredSignal.get()){finish(StopReason.DENIED,"authorization_timeout");return;}
                    if(!Boolean.TRUE.equals(receipt.result.get("allowed"))){
                        rememberHold(receipt.result,runId);
                        Object retry=receipt.result.get("retry_at");
                        if("instance-limit".equals(receipt.result.get("code"))&&runId.equals(receipt.result.get("run_id"))&&retry instanceof Long&&(Long)retry>0){
                            long nowMillis=wallMillis.getAsLong(),seconds=Math.min(301L,Math.max(0L,(Long)retry-nowMillis/1000));
                            long delay=Math.min(300000L,Math.max(1000L,seconds*1000L-nowMillis%1000))+ThreadLocalRandom.current().nextLong(1001);
                            nextAcquire=monotonicNanos.getAsLong()+delay*1000000L;retryWallMillis=wallMillis.getAsLong()+delay;
                            waitingForSlot=true;startDeadline=0;watchdogNanos=0;watchdogWallMillis=0;stopWatchdog();lastReason=reason();return;
                        }
                        finish(StopReason.DENIED,String.valueOf(receipt.result.get("code")));return;
                    }
                    startDeadline=monotonicNanos.getAsLong()+30000000000L;watchdogWallMillis=wallMillis.getAsLong()+30000;
                    applyPermit(receipt,receipt.result);
                    if(epoch!=generation||state!=State.STARTING)return;
                    entered=true;
                    if(!bot.start()||monotonicNanos.getAsLong()>=startDeadline||expired())throw new IllegalStateException("startup_failed");
                    if(epoch!=generation||state!=State.STARTING)return;
                    state=State.RUNNING;watchdogNanos=permitDeadline;watchdogWallMillis=permitWallMillis;hostDeadline();
                    if(epoch!=generation||state!=State.RUNNING)return;
                    nextReport=0;lastReason="started";startFuture.complete(new StartResult(true,"started"));report();
                }catch(Throwable failure){finish(StopReason.START_FAILED,"startup_failed");}
            }
        });
    }
    /** Finite async work completes the managed run when its stage settles. */
    public CompletionStage<StartResult> run(Supplier<? extends CompletionStage<?>> work){
        Objects.requireNonNull(work);
        return run(cancellation->work.get());
    }
    /** Owned work must observe cancellation; completion cleanup runs on the owner thread. */
    public CompletionStage<StartResult> run(Function<Cancellation,? extends CompletionStage<?>> work){
        Objects.requireNonNull(work);
        final long generation;final CompletionStage<StartResult> admission;
        synchronized(this){
            if(finitePending)return CompletableFuture.completedFuture(new StartResult(false,"task_already_running"));
            finitePending=true;admission=start();generation=epoch;
        }
        return admission.thenApply(result->{synchronized(this){
            if(!result.started||epoch!=generation||!active()){
                if(epoch==generation)finitePending=false;
                return new StartResult(false,result.started?"cancelled":result.reason);
            }
            try{
                CompletionStage<?> stage=Objects.requireNonNull(work.apply(()->epoch!=generation||expired()));
                if(epoch!=generation){if(stage instanceof Future<?>)((Future<?>)stage).cancel(true);return new StartResult(false,"cancelled");}
                task=stage;
                stage.whenComplete((value,error)->client.dispatch(()->{synchronized(this){
                    if(task==stage&&epoch==generation){task=null;finish(error==null?StopReason.COMPLETED:StopReason.CALLBACK_FAILED,error==null?"completed":"task_failed");}
                }}));
            }catch(Throwable failure){finish(StopReason.CALLBACK_FAILED,"task_failed");}
            return result;
        }});
    }
    public synchronized void stop(){finish(StopReason.USER,"stopped");}
    public synchronized void complete(){finish(StopReason.COMPLETED,"completed");}
    private void rememberHold(Map<String,Object> result,String expectedRun){
        if(!client.sessionValid()||!Objects.equals(expectedRun,result.get("run_id")))return;
        Object held=result.get("held_until");
        if(held instanceof Long&&(Long)held>0)releasePendingUntil=Math.max(releasePendingUntil,(Long)held);
    }
    private void applyPermit(ReceiptVerifier.Receipt receipt,Map<String,Object> permit){
        if(!runId.equals(permit.get("run_id")))throw new IllegalArgumentException("wrong_run");
        String lease=Json.string(permit,"lease_id");long revision=Json.number(permit,"permit_sequence");
        long deadline=receipt.permitDeadline(permit);
        if(lease.isEmpty()||revision<=0||leaseId!=null&&!leaseId.equals(lease)||revision<permitSequence||deadline<=monotonicNanos.getAsLong())throw new IllegalArgumentException("invalid_permit");
        if(revision==permitSequence&&permitSequence!=0)deadline=Math.min(deadline,permitDeadline);
        long wall=Json.number(permit,"permit_expires_at")*1000;
        if(revision==permitSequence&&permitSequence!=0)wall=Math.min(wall,permitWallMillis);
        leaseId=lease;permitSequence=revision;permitDeadline=deadline;permitWallMillis=wall;
        releasePendingUntil=Math.max(releasePendingUntil,wall/1000);rememberHold(permit,runId);
        if(running()){watchdogNanos=deadline;watchdogWallMillis=wall;}
        else {watchdogNanos=Math.min(startDeadline,deadline);watchdogWallMillis=Math.min(watchdogWallMillis,wall);}
        hostDeadline();
    }
    private synchronized void pulse(){
        long now=monotonicNanos.getAsLong();
        if(state==State.STARTING){
            if(waitingForSlot){if(now>=nextAcquire||wallMillis.getAsLong()>=retryWallMillis)acquire();}
            else if(now>=startDeadline||expiredSignal.get()||entered&&expired())finish(StopReason.START_FAILED,entered?"startup_timeout":"authorization_timeout");
            return;
        }
        if(!running())return;
        if(expired()){finish(StopReason.PERMIT_EXPIRED,"permit_expired");return;}
        if(now>=nextReport&&!reportPending)report();
    }
    private void report(){
        if(!running()||reportPending)return;
        if(expired()){finish(StopReason.PERMIT_EXPIRED,"permit_expired");return;}
        long generation=epoch;reportPending=true;nextReport=Math.max(monotonicNanos.getAsLong()+1000000000L,Math.min(monotonicNanos.getAsLong()+20000000000L,permitDeadline-10000000000L));
        Map<String,Object> run=Json.map("run_id",runId,"lease_id",leaseId,"ack_sequence",permitSequence,"state","running");
        client.request("report",Json.map("sequence",++sequence,"complete",true,"runs",Collections.singletonList(run)),null).whenComplete((receipt,error)->{
            synchronized(Instance.this){
                if(epoch!=generation||!running())return;reportPending=false;
                if(expired()){finish(StopReason.PERMIT_EXPIRED,"permit_expired");return;}
                if(error!=null){state=State.RECONNECTING;lastReason="authorization_unavailable";long cap=Math.min(15000L,1000L<<Math.min(retryAttempt++,4));nextReport=monotonicNanos.getAsLong()+ThreadLocalRandom.current().nextLong(Math.max(1,cap/2),cap+1)*1000000L;return;}
                try {
                    rememberHold(receipt.result,runId);
                    if(Boolean.FALSE.equals(receipt.result.get("allowed"))){finish(StopReason.DENIED,String.valueOf(receipt.result.get("code")));return;}
                    if(!Boolean.TRUE.equals(receipt.result.get("complete")))throw new IllegalArgumentException("incomplete_report");
                    Object runs=receipt.result.get("runs");if(!(runs instanceof List)||((List<?>)runs).size()!=1)throw new IllegalArgumentException();
                    Map<String,Object> update=Json.object(((List<?>)runs).get(0));
                    rememberHold(update,runId);
                    if(!Boolean.TRUE.equals(update.get("allowed"))){finish(StopReason.DENIED,String.valueOf(update.get("code")));return;}
                    applyPermit(receipt,update);
                    if(epoch!=generation||!running())return;
                    state=State.RUNNING;lastReason="";retryAttempt=0;
                }catch(Throwable failure){finish(StopReason.DENIED,"invalid_permit");}
            }
        });
    }
    private synchronized void tick(int tick){if(!active())return;try{bot.tick(tick);}catch(Throwable failure){finish(StopReason.CALLBACK_FAILED,"callback_failed");}}
    private synchronized void runtimeStop(String reason){
        if("session_changed".equals(reason)){identityEpoch++;releasePendingUntil=0;}
        finish("unloaded".equals(reason)?StopReason.UNLOADED:"session_changed".equals(reason)?StopReason.SESSION_CHANGED:StopReason.DISABLED,reason);
    }
    private void finish(StopReason reason,String detail){
        if(state==State.STOPPED||state==State.STOPPING||state==State.FAILED)return;
        ++epoch;state=State.STOPPING;lastReason=detail;watchdogNanos=0;watchdogWallMillis=0;reportPending=false;finitePending=false;waitingForSlot=false;client.cancelPending(detail,true);
        stopWatchdog();
        if(runId!=null){
            String cancelledRun=runId;long identity=identityEpoch;
            Map<String,Object> cancel=Json.map("run_id",cancelledRun,"sequence",++sequence);if(leaseId!=null)cancel.put("lease_id",leaseId);
            client.request("cancel",cancel,cancelledRun).whenComplete((receipt,error)->{synchronized(Instance.this){
                if(error==null&&!closed&&identity==identityEpoch)rememberHold(receipt.result,cancelledRun);
            }});
        }
        client.stopTransport(); // Runs even if the user stop hook throws or blocks.
        CompletableFuture<StartResult> future=startFuture;if(future!=null&&!future.isDone())future.complete(new StartResult(false,detail));
        CompletionStage<?> finite=task;task=null;if(finite instanceof Future<?>)((Future<?>)finite).cancel(true);
        boolean cleanup=entered;entered=false;permitDeadline=0;
        try{if(cleanup)bot.stop(reason);}catch(Throwable ignored){}finally{
            state=reason==StopReason.START_FAILED||reason==StopReason.CALLBACK_FAILED?State.FAILED:State.STOPPED;
        }
    }
    @Override public synchronized void close(){stop();closed=true;client.close();}
}
