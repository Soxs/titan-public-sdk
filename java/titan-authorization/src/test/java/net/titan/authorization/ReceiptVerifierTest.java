package net.titan.authorization;

import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ReceiptVerifierTest {
    Map<String,Object> fixture,context,bindings,cases;
    ReceiptVerifier verifier;
    @BeforeEach void fixture() throws Exception {
        fixture=Json.object(Files.readString(Path.of(System.getProperty("titanAuthorizationFixture"))));
        context=Json.object(fixture.get("context"));bindings=Json.object(fixture.get("bindings"));cases=Json.object(fixture.get("cases"));
        String pem=Json.string(fixture,"public_key_pem").replaceAll("-----[^-]+-----|\\s","");
        verifier=new ReceiptVerifier(Collections.singletonMap(Json.string(fixture,"kid"),pem),()->Json.number(fixture,"now"),()->1000000000L);
    }
    Map<String,Object> response(String name){Map<String,Object> value=new LinkedHashMap<>(Json.object(cases.get(name)));value.put("caller_context",fixture.get("caller_context"));return value;}
    ReceiptVerifier.Receipt verify(Map<String,Object> value,String op,String request,String challenge,Map<String,Object> ctx,String run){
        return verifier.verify(Json.write(value),op,request,challenge,ctx,run,1000000000L);
    }
    @Test void acceptsServerGeneratedCrossLanguageFixtures(){
        for(String name:Arrays.asList("acquire","report","check","list","denied")){
            String op=name.equals("denied")?"acquire":name;
            ReceiptVerifier.Receipt result=verify(response(name),op,Json.string(bindings,"request_id"),Json.string(bindings,"challenge"),context,null);
            assertEquals(op,result.result.get("operation"));assertEquals(301000000000L,result.deadlineNanos);
        }
    }
    ReceiptVerifier fixtureVerifier(long wallOffset,long monotonicNanos){
        String pem=Json.string(fixture,"public_key_pem").replaceAll("-----[^-]+-----|\\s","");
        return new ReceiptVerifier(Collections.singletonMap(Json.string(fixture,"kid"),pem),
            ()->Json.number(fixture,"now")+wallOffset,()->monotonicNanos);
    }
    @Test void delayedResponsesDoNotChargeTransitTwice(){
        verifier=fixtureVerifier(100,101000000000L);
        for(String name:Arrays.asList("acquire","report","check","list","denied")){
            String op=name.equals("denied")?"acquire":name;
            ReceiptVerifier.Receipt result=verify(response(name),op,Json.string(bindings,"request_id"),Json.string(bindings,"challenge"),context,null);
            assertEquals(301000000000L,result.deadlineNanos,"Transit must not shorten a valid proof twice");
        }
        verifier=fixtureVerifier(299,300000000000L);
        assertEquals(301000000000L,verifyAcquire().deadlineNanos,"A delayed retry must keep the original deadline");
    }
    ReceiptVerifier.Receipt verifyAcquire(){
        return verify(response("acquire"),"acquire",Json.string(bindings,"request_id"),Json.string(bindings,"challenge"),context,null);
    }
    @Test void clockChangesCannotExtendRequestLifetimeOrAcceptExpiredProof(){
        verifier=fixtureVerifier(-20,300000000000L);
        assertEquals(301000000000L,verifyAcquire().deadlineNanos);
        verifier=fixtureVerifier(-20,301000000000L);
        assertThrows(IllegalArgumentException.class,this::verifyAcquire);
        verifier=fixtureVerifier(300,1000000000L);
        assertThrows(IllegalArgumentException.class,this::verifyAcquire);
        verifier=fixtureVerifier(0,999999999L);
        assertThrows(IllegalArgumentException.class,this::verifyAcquire);
    }
    @Test void shortProofRemainsValidAfterMostOfItsLifetimeWasSpentInTransit() throws Exception {
        ReceiptVerifier.Receipt result=shortReceipt(0,10,10);
        assertEquals(11000000000L,result.deadlineNanos);
        assertEquals(11000000000L,result.permitDeadline(result.result));
        Map<String,Object> laterPermit=new LinkedHashMap<>(result.result);
        laterPermit.put("permit_expires_at",Json.number(fixture,"now")+30);
        assertEquals(11000000000L,result.permitDeadline(laterPermit),"A permit cannot extend the signed receipt lifetime");
    }
    @Test void earlierPermitExpiryDoesNotSubtractServerDelayAgain() throws Exception {
        ReceiptVerifier.Receipt result=shortReceipt(6,16,9);
        assertEquals(11000000000L,result.deadlineNanos,"Delayed issuance remains bounded by request lifetime");
        assertEquals(10000000000L,result.permitDeadline(result.result),"Permit wall expiry must be measured from response observation");
    }
    ReceiptVerifier.Receipt shortReceipt(long issuedOffset,long expiryOffset,long permitOffset) throws Exception {
        KeyPairGenerator generator=KeyPairGenerator.getInstance("RSA");generator.initialize(2048);
        KeyPair keys=generator.generateKeyPair();long now=Json.number(fixture,"now");
        String certificate=sign(context,keys);
        Map<String,Object> value=response("acquire");
        Map<String,Object> result=new LinkedHashMap<>(Json.object(Json.string(value,"result_json")));
        result.put("permit_expires_at",now+permitOffset);String resultText=Json.write(result);
        String[] parts=Json.string(value,"proof").split("\\.");
        Map<String,Object> claims=new LinkedHashMap<>(Json.object(new String(Base64.getUrlDecoder().decode(parts[1]),StandardCharsets.UTF_8)));
        claims.putAll(Json.map("iat",now+issuedOffset,"nbf",now+issuedOffset,"exp",now+expiryOffset,
            "caller_context_sha256",hash(certificate),"result_sha256",hash(resultText)));
        value.putAll(Json.map("caller_context",certificate,"proof",sign(claims,keys),"result_json",resultText));
        verifier=new ReceiptVerifier(Collections.singletonMap("test",Base64.getEncoder().encodeToString(keys.getPublic().getEncoded())),
            ()->now+6,()->7000000000L);
        return verify(value,"acquire",Json.string(bindings,"request_id"),Json.string(bindings,"challenge"),context,null);
    }
    static String sign(Map<String,Object> claims,KeyPair keys) throws Exception {
        Base64.Encoder encoder=Base64.getUrlEncoder().withoutPadding();
        String header=encoder.encodeToString(Json.write(Json.map("alg","RS256","typ","titan-authorization+jwt","kid","test")).getBytes(StandardCharsets.UTF_8));
        String data=header+"."+encoder.encodeToString(Json.write(claims).getBytes(StandardCharsets.UTF_8));
        Signature signature=Signature.getInstance("SHA256withRSA");signature.initSign(keys.getPrivate());signature.update(data.getBytes(StandardCharsets.UTF_8));
        return data+"."+encoder.encodeToString(signature.sign());
    }
    static String hash(String value) throws Exception {
        StringBuilder hex=new StringBuilder();
        for(byte b:MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)))hex.append(String.format("%02x",b&255));
        return hex.toString();
    }
    @Test void rejectsTamperedResultSignatureAndSession(){
        Map<String,Object> value=response("acquire");value.put("result_json",Json.string(value,"result_json")+" ");
        assertThrows(IllegalArgumentException.class,()->verify(value,"acquire",Json.string(bindings,"request_id"),Json.string(bindings,"challenge"),context,null));
        Map<String,Object> other=new LinkedHashMap<>(context);other.put("runtime_generation",4L);
        assertThrows(IllegalArgumentException.class,()->verify(response("acquire"),"acquire",Json.string(bindings,"request_id"),Json.string(bindings,"challenge"),other,null));
        assertThrows(IllegalArgumentException.class,()->verify(response("acquire"),"acquire","different-request",Json.string(bindings,"challenge"),context,null));
        assertThrows(IllegalArgumentException.class,()->verify(response("acquire"),"acquire",Json.string(bindings,"request_id"),Json.string(bindings,"challenge"),context,"different-run"));
    }
    @Test void rejectsUnknownKeyAndExpiredProof(){
        verifier=new ReceiptVerifier(Collections.emptyMap(),()->Json.number(fixture,"now"),()->1000000000L);
        assertThrows(IllegalArgumentException.class,()->verify(response("check"),"check",Json.string(bindings,"request_id"),Json.string(bindings,"challenge"),context,null));
    }
    @Test void strictJsonRejectsDuplicatesUnsafeNumbersAndInvalidUnicode(){
        for(String bad:Arrays.asList("{\"a\":1,\"a\":2}","{\"a\":{\"x\":1,\"x\":2}}","9007199254740992","1.0","01","\"\\ud800\""))
            assertThrows(IllegalArgumentException.class,()->Json.parse(bad));
        assertEquals("😀",Json.parse("\"\\ud83d\\ude00\""));
        assertEquals(Boolean.TRUE,Json.object("{\"__proto__\":true}").get("__proto__"));
    }
}
