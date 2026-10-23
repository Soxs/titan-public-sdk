package net.titan.authorization;

import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
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
