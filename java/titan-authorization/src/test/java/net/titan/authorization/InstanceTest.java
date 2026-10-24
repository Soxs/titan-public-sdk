package net.titan.authorization;

import net.titan.api.internal.*;
import net.titan.api.plugins.*;
import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class InstanceTest {
    @PluginDescriptor(id="fixture_bot",name="Fixture",description="Test",author="Test",version="1")
    static class Owner implements Plugin {}
    static class Bot implements Instance.Bot {
        int starts,stops,ticks;boolean accept=true,throwStop;Thread stoppedOn;
        public boolean start(){starts++;return accept;}
        public void tick(int tick){ticks++;}
        public void stop(Instance.StopReason reason){stops++;stoppedOn=Thread.currentThread();if(throwStop)throw new RuntimeException();}
    }
    KeyPair keys;Map<String,Object> context,fixture;Backend backend;ReceiptVerifier verifier;
    long fakeNanos,fakeMillis;boolean fakeTime;
    @BeforeEach void setup() throws Exception {
        fixture=Json.object(Files.readString(Path.of(System.getProperty("titanAuthorizationFixture"))));
        keys=KeyPairGenerator.getInstance("RSA").generateKeyPair();
        long now=System.currentTimeMillis()/1000;
        context=new LinkedHashMap<>(Json.object(fixture.get("context")));context.put("iat",now);context.put("nbf",now);context.put("exp",now+1800);
        verifier=new ReceiptVerifier(Collections.singletonMap("test",Base64.getEncoder().encodeToString(keys.getPublic().getEncoded())));
        backend=new Backend();TitanRuntime.setAuthorizationBackend(backend);
    }
    Instance timed(Bot bot){
        fakeTime=true;fakeNanos=1000000000L;fakeMillis=System.currentTimeMillis();
        ReceiptVerifier timedVerifier=new ReceiptVerifier(Collections.singletonMap("test",Base64.getEncoder().encodeToString(keys.getPublic().getEncoded())),()->fakeMillis/1000,()->fakeNanos);
        return new Instance(new Owner(),bot,timedVerifier,()->fakeNanos,()->fakeMillis);
    }
    void advance(long millis){fakeNanos+=millis*1000000L;fakeMillis+=millis;}
    @AfterEach void teardown(){TitanRuntime.setAuthorizationBackend(null);}
    String sign(Map<String,Object> claims){try{
        Base64.Encoder b=Base64.getUrlEncoder().withoutPadding();
        String head=b.encodeToString(Json.write(Json.map("alg","RS256","typ","titan-authorization+jwt","kid","test")).getBytes(StandardCharsets.UTF_8));
        String data=head+"."+b.encodeToString(Json.write(claims).getBytes(StandardCharsets.UTF_8));
        Signature signer=Signature.getInstance("SHA256withRSA");signer.initSign(keys.getPrivate());signer.update(data.getBytes(StandardCharsets.UTF_8));
        return data+"."+b.encodeToString(signer.sign());
    }catch(Exception error){throw new AssertionError(error);}}
    static String hash(String value){try{byte[] bytes=MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));StringBuilder s=new StringBuilder();for(byte b:bytes)s.append(String.format("%02x",b));return s.toString();}catch(Exception e){throw new AssertionError(e);}}
    class Backend implements AuthorizationBackend {
        Set<Listener> listeners=new LinkedHashSet<>();Map<Long,Map<String,Object>> requests=new LinkedHashMap<>();
        List<String> operations=new ArrayList<>();Map<String,Long> holds=new HashMap<>();long next,retryAt,cancelHeldUntil;boolean delay,denied,tamper,stopOnDeadline;int stops;String incompleteReport;
        public boolean register(Plugin owner,Listener listener){listeners.add(listener);return true;}
        public void unregister(Plugin owner,Listener listener){listeners.remove(listener);}
        public String context(Plugin owner){return Json.write(context);}
        public long submit(Plugin owner,String operation,String raw){Map<String,Object> item=new LinkedHashMap<>(Json.object(raw));item.put("operation",operation);requests.put(++next,item);operations.add(operation);return next;}
        public void cancel(Plugin owner,long ticket){requests.remove(ticket);}
        public void authorizationStop(Plugin owner){stops++;requests.entrySet().removeIf(entry->!"cancel".equals(entry.getValue().get("operation")));}
        public void deadline(Plugin owner,long remainingMillis){if(stopOnDeadline){stopOnDeadline=false;context.put("session_valid",false);stopOwner("session_changed");}}
        public String poll(Plugin owner,long ticket){
            if(delay)return "";Map<String,Object> request=requests.remove(ticket);if(request==null)return "";
            String operation=Json.string(request,"operation"),certificate=sign(context);long now=(fakeTime?fakeMillis:System.currentTimeMillis())/1000;
            Map<String,Object> payload=Json.object(request.get("payload"));
            Map<String,Object> permit=Json.map("run_id",payload.get("run_id"),"lease_id","lease","permit_sequence",1L,"permit_expires_at",now+120,"held_until",now+120,"allowed",!denied,"code",denied?"instance-limit":null);
            Map<String,Object> result=new LinkedHashMap<>(permit);result.put("operation",operation);
            if(operation.equals("acquire")){
                if(denied){result.remove("held_until");result.remove("lease_id");result.remove("permit_expires_at");if(retryAt>0)result.put("retry_at",retryAt);}
                else holds.put(Json.string(payload,"run_id"),now+120);
            }
            if(operation.equals("report")){Map<String,Object> run=Json.object(((List<?>)payload.get("runs")).get(0));permit.put("run_id",run.get("run_id"));permit.put("permit_sequence",Json.number(run,"ack_sequence")+1);result.put("complete",true);result.put("runs",Collections.singletonList(permit));holds.put(Json.string(run,"run_id"),now+120);}
            if(operation.equals("report")&&incompleteReport!=null){if(incompleteReport.equals("missing"))result.remove("complete");else result.put("complete",incompleteReport.equals("false")?Boolean.FALSE:"true");}
            if(operation.equals("cancel"))result=Json.map("operation","cancel","allowed",false,"run_id",payload.get("run_id"),"held_until",cancelHeldUntil>0?cancelHeldUntil:holds.getOrDefault(Json.string(payload,"run_id"),0L));
            String body=Json.write(result);Map<String,Object> claims=new LinkedHashMap<>(context);
            claims.putAll(Json.map("iat",now,"nbf",now,"exp",now+300,"purpose","plugin_run","operation",operation,"request_id",request.get("request_id"),"challenge",request.get("challenge"),"caller_context_sha256",hash(certificate),"result_sha256",hash(body)));
            if(payload.containsKey("run_id"))claims.put("run_id",payload.get("run_id"));
            return Json.write(Json.map("caller_context",certificate,"proof",sign(claims),"result_json",body+(tamper?" ":"")));
        }
        void pulse(){for(Listener listener:new ArrayList<>(listeners))listener.pulse();}
        void stopOwner(String reason){for(Listener listener:new ArrayList<>(listeners))listener.stopped(reason);}
    }
    @Test void partialOrMalformedSignedReportCannotRenew(){
        for(String incomplete:Arrays.asList("missing","false","wrong-type")){
            backend=new Backend();TitanRuntime.setAuthorizationBackend(backend);backend.incompleteReport=incomplete;
            Bot bot=new Bot();Instance run=timed(bot);CompletionStage<Instance.StartResult> start=run.start();backend.pulse();assertTrue(start.toCompletableFuture().join().started);
            backend.pulse();assertFalse(run.active());assertEquals("invalid_permit",run.reason());assertEquals(1,bot.stops);run.close();
        }
    }
    @Test void hugeSignedRetryHintIsBoundedBeforeTimestampArithmetic(){
        Instance run=timed(new Bot());backend.denied=true;backend.retryAt=9007199254740991L;
        run.start();backend.pulse();advance(299999);backend.pulse();assertEquals(1,Collections.frequency(backend.operations,"acquire"));
        advance(1001);backend.pulse();assertEquals(2,Collections.frequency(backend.operations,"acquire"));run.close();
    }
    @Test void signedCapacityWaitCoalescesAndOutlastsStartupBudget(){
        Bot bot=new Bot();Instance run=timed(bot);backend.denied=true;backend.retryAt=fakeMillis/1000+60;
        CompletionStage<Instance.StartResult> start=run.start();backend.pulse();
        assertEquals(Instance.State.STARTING,run.status());assertSame(start,run.start());assertEquals(0,run.releasePendingUntil());assertTrue(run.reason().contains("Waiting for an instance slot"));
        advance(31000);backend.pulse();assertEquals(Instance.State.STARTING,run.status());assertEquals(1,Collections.frequency(backend.operations,"acquire"));
        backend.denied=false;advance(30000);backend.pulse();backend.pulse();
        assertTrue(start.toCompletableFuture().join().started);assertEquals(1,bot.starts);assertEquals(2,Collections.frequency(backend.operations,"acquire"));
        assertEquals(fakeMillis/1000+120,run.releasePendingUntil());run.close();
    }
    @Test void identityChangeWhileArmingPermitCannotStartOrResurrect(){
        for(boolean duringStart:Arrays.asList(false,true)){
            context.remove("session_valid");backend=new Backend();TitanRuntime.setAuthorizationBackend(backend);backend.stopOnDeadline=duringStart;
            Bot bot=new Bot();Instance run=timed(bot);CompletionStage<Instance.StartResult> start=run.start();backend.pulse();
            if(!duringStart){assertTrue(start.toCompletableFuture().join().started);backend.stopOnDeadline=true;backend.pulse();}
            else assertFalse(start.toCompletableFuture().join().started);
            assertEquals(Instance.State.STOPPED,run.status());assertEquals("session_changed",run.reason());
            assertEquals(duringStart?0:1,bot.starts);assertEquals(duringStart?0:1,bot.stops);run.close();
        }
    }
    @Test void hostIdentityInvalidationStopsRunningAndWaitingWithoutNewRequest(){
        for(boolean waiting:Arrays.asList(false,true)){
            context.remove("session_valid");backend=new Backend();TitanRuntime.setAuthorizationBackend(backend);
            Bot bot=new Bot();Instance run=timed(bot);backend.denied=waiting;backend.retryAt=fakeMillis/1000+60;
            CompletionStage<Instance.StartResult> start=run.start();backend.pulse();
            context.put("session_valid",false);backend.pulse();
            assertFalse(run.active());assertEquals("session_changed",run.reason());assertEquals(0,run.releasePendingUntil());
            assertFalse(run.start().toCompletableFuture().join().started);assertEquals(waiting?0:1,bot.starts);
            assertEquals(1,Collections.frequency(backend.operations,"acquire"));start.toCompletableFuture().join();run.close();
        }
    }
    @Test void callbackGetsFreshThirtySecondBudgetAfterCapacityWait(){
        Bot bot=new Bot(){@Override public boolean start(){starts++;advance(29000);return true;}};
        Instance run=timed(bot);backend.denied=true;backend.retryAt=fakeMillis/1000+60;
        CompletionStage<Instance.StartResult> start=run.start();backend.pulse();backend.denied=false;advance(61000);backend.pulse();backend.pulse();
        assertTrue(start.toCompletableFuture().join().started);assertEquals(1,bot.starts);run.close();
        Bot slow=new Bot(){@Override public boolean start(){starts++;advance(31000);return true;}};
        Instance timedOut=timed(slow);CompletionStage<Instance.StartResult> attempt=timedOut.start();backend.pulse();
        assertFalse(attempt.toCompletableFuture().join().started);assertEquals(1,slow.stops);assertFalse(timedOut.active());timedOut.close();
    }
    @Test void stopUnloadAndIdentityCancelCapacityWaitWithoutResurrection(){
        for(String end:Arrays.asList("stop","close","unloaded","session_changed")){
            backend=new Backend();TitanRuntime.setAuthorizationBackend(backend);
            Bot bot=new Bot();Instance run=timed(bot);backend.denied=true;backend.retryAt=fakeMillis/1000+60;
            CompletionStage<Instance.StartResult> start=run.start();backend.pulse();
            if(end.equals("stop"))run.stop();else if(end.equals("close"))run.close();else backend.stopOwner(end);
            assertFalse(start.toCompletableFuture().join().started);
            assertTrue(backend.requests.values().stream().anyMatch(request->"cancel".equals(request.get("operation"))),"Terminal cancellation survives SDK close");
            backend.denied=false;advance(61000);backend.pulse();assertEquals(0,bot.starts);assertEquals(1,Collections.frequency(backend.operations,"acquire"));run.close();
        }
    }
    @Test void pastRetryTimestampCannotBusyLoopAndIdentityChangeCannotSubmitOldStart(){
        Instance run=timed(new Bot());backend.denied=true;backend.retryAt=fakeMillis/1000-1;
        CompletionStage<Instance.StartResult> start=run.start();backend.pulse();
        for(int i=0;i<5;i++)backend.pulse();assertEquals(1,Collections.frequency(backend.operations,"acquire"));
        context.put("runtime_generation",4L);advance(1000);backend.pulse();
        assertFalse(start.toCompletableFuture().join().started);assertEquals("session_changed",run.reason());
        assertEquals(1,Collections.frequency(backend.operations,"acquire"));run.close();
    }
    @Test void verifiedHoldPersistsAcrossFailedRestartAndLateCancelReceipt(){
        Instance run=timed(new Bot());CompletionStage<Instance.StartResult> first=run.start();assertEquals(0,run.releasePendingUntil());backend.pulse();assertTrue(first.toCompletableFuture().join().started);
        long hold=run.releasePendingUntil();assertEquals(fakeMillis/1000+120,hold);
        backend.delay=true;advance(20000);backend.pulse();assertEquals(hold,run.releasePendingUntil());run.stop();
        backend.cancelHeldUntil=hold+30;CompletionStage<Instance.StartResult> second=run.start();advance(31000);backend.pulse();
        assertFalse(second.toCompletableFuture().join().started);assertEquals(hold,run.releasePendingUntil());
        backend.delay=false;backend.pulse();assertEquals(hold+30,run.releasePendingUntil());run.close();
    }
    @Test void admissionTimeoutAndUnsignedFailureNeverInventReservationOrRestart(){
        Instance run=timed(new Bot());backend.delay=true;CompletionStage<Instance.StartResult> start=run.start();
        advance(30001);backend.pulse();assertFalse(start.toCompletableFuture().join().started);assertEquals(0,run.releasePendingUntil());
        backend.delay=false;advance(120000);backend.pulse();assertFalse(run.active());assertEquals(1,Collections.frequency(backend.operations,"acquire"));run.close();
    }
    @Test void admissionDuplicateStartAndThrowingCleanup(){
        Bot bot=new Bot();bot.throwStop=true;Instance run=new Instance(new Owner(),bot,verifier);
        CompletionStage<Instance.StartResult> start=run.start();assertSame(start,run.start());assertEquals(0,bot.starts);
        backend.pulse();assertTrue(start.toCompletableFuture().join().started);assertEquals(1,bot.starts);
        run.stop();assertEquals(1,bot.stops);assertEquals(1,backend.stops);assertEquals(Instance.State.STOPPED,run.status());
        assertTrue(run.releasePendingUntil()>0);assertEquals(1,Collections.frequency(backend.operations,"acquire"));
    }
    @Test void deniedOrForgedResponseNeverEntersStartup(){
        for(int variant=0;variant<2;variant++){
            backend.denied=variant==0;backend.tamper=variant==1;Bot bot=new Bot();Instance run=new Instance(new Owner(),bot,verifier);
            CompletionStage<Instance.StartResult> start=run.start();backend.pulse();assertFalse(start.toCompletableFuture().join().started);assertEquals(0,bot.starts);run.close();
        }
    }
    @Test void stopPendingAdmissionOmitsUnknownLease(){
        backend.delay=true;Bot bot=new Bot();Instance run=new Instance(new Owner(),bot,verifier);
        CompletionStage<Instance.StartResult> start=run.start();run.stop();assertFalse(start.toCompletableFuture().join().started);
        Map<String,Object> cancellation=backend.requests.values().iterator().next();assertFalse(Json.object(cancellation.get("payload")).containsKey("lease_id"));
        backend.delay=false;backend.pulse();assertEquals(0,bot.starts);
    }
    @Test void partialStartupCleansUpOnce(){
        Bot bot=new Bot();bot.accept=false;Instance run=new Instance(new Owner(),bot,verifier);
        CompletionStage<Instance.StartResult> start=run.start();backend.pulse();assertFalse(start.toCompletableFuture().join().started);assertEquals(1,bot.stops);run.stop();assertEquals(1,bot.stops);
    }
    @Test void backgroundTaskCompletionCleansUpOnOwnerPulse() throws Exception {
        Bot bot=new Bot();Instance run=new Instance(new Owner(),bot,verifier);CompletableFuture<Void> task=new CompletableFuture<>();
        run.run(cancellation->task);backend.pulse();assertTrue(run.active());
        Thread worker=new Thread(()->task.complete(null));worker.start();worker.join();assertEquals(0,bot.stops);
        backend.pulse();assertEquals(1,bot.stops);assertSame(Thread.currentThread(),bot.stoppedOn);assertFalse(run.active());
    }
    @Test void lateRenewalCannotReviveExpiredRun() throws Exception {
        Bot bot=new Bot();Instance run=new Instance(new Owner(),bot,verifier);
        run.start();backend.pulse();assertTrue(run.active());
        java.lang.reflect.Field deadline=Instance.class.getDeclaredField("permitDeadline");deadline.setAccessible(true);deadline.setLong(run,System.nanoTime()-1);
        backend.pulse();assertFalse(run.active());assertEquals(1,bot.stops);assertEquals("permit_expired",run.reason());
    }
    @Test void watchdogClosesCancellationBeforeOwnerCleanup() throws Exception {
        Bot bot=new Bot();Instance run=new Instance(new Owner(),bot,verifier);CompletableFuture<Void> task=new CompletableFuture<>();
        Instance.Cancellation[] cancellation=new Instance.Cancellation[1];
        run.run(token->{cancellation[0]=token;return task;});backend.pulse();
        java.lang.reflect.Field deadline=Instance.class.getDeclaredField("watchdogNanos");deadline.setAccessible(true);deadline.setLong(run,System.nanoTime()-1);
        java.lang.reflect.Method watchdog=Instance.class.getDeclaredMethod("watchdog");watchdog.setAccessible(true);watchdog.invoke(run);
        assertTrue(cancellation[0].cancelled());assertEquals(0,bot.stops);
        backend.pulse();assertEquals(1,bot.stops);assertSame(Thread.currentThread(),bot.stoppedOn);
    }
    @Test void watchdogExecutorEndsWithRunAndIsRecreatedForNextStart() throws Exception {
        Instance run=new Instance(new Owner(),new Bot(),verifier);
        java.lang.reflect.Field executorField=Instance.class.getDeclaredField("watchdogExecutor");executorField.setAccessible(true);
        java.lang.reflect.Field taskField=Instance.class.getDeclaredField("watchdogTask");taskField.setAccessible(true);
        run.start();backend.pulse();
        ScheduledExecutorService first=(ScheduledExecutorService)executorField.get(run);
        ScheduledFuture<?> firstTask=(ScheduledFuture<?>)taskField.get(run);
        run.stop();assertTrue(first.isShutdown());assertTrue(firstTask.isCancelled());assertNull(executorField.get(run));assertNull(taskField.get(run));
        assertTrue(first.awaitTermination(1,TimeUnit.SECONDS));
        run.start();backend.pulse();
        ScheduledExecutorService second=(ScheduledExecutorService)executorField.get(run);
        ScheduledFuture<?> secondTask=(ScheduledFuture<?>)taskField.get(run);
        assertNotSame(first,second);run.close();
        assertTrue(second.isShutdown());assertTrue(secondTask.isCancelled());assertNull(executorField.get(run));assertNull(taskField.get(run));
        assertTrue(second.awaitTermination(1,TimeUnit.SECONDS));
    }
}
