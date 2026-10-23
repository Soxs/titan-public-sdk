package net.titan.authorization;

import java.nio.ByteBuffer;
import java.nio.charset.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/** This class is packaged in, and relocated with, every consuming plugin. */
final class ReceiptVerifier {
    private final Map<String,String> keys;
    private final java.util.function.LongSupplier wallSeconds, monotonicNanos;
    ReceiptVerifier() { this(KeyRing.keys()); }
    ReceiptVerifier(Map<String,String> keys) { this(keys,()->System.currentTimeMillis()/1000,System::nanoTime); }
    ReceiptVerifier(Map<String,String> keys,java.util.function.LongSupplier wall,java.util.function.LongSupplier monotonic) {
        this.keys=Collections.unmodifiableMap(new HashMap<>(keys));wallSeconds=wall;monotonicNanos=monotonic;
    }
    static final class Receipt {
        final Map<String,Object> claims, result;
        final long deadlineNanos;
        Receipt(Map<String,Object> claims, Map<String,Object> result, long deadlineNanos) {
            this.claims=claims; this.result=result; this.deadlineNanos=deadlineNanos;
        }
        long permitDeadline(Map<String,Object> permit) {
            long seconds = Json.number(permit,"permit_expires_at") - Json.number(claims,"exp");
            return Math.min(deadlineNanos, deadlineNanos + seconds * 1000000000L);
        }
    }
    Receipt verify(String response, String operation, String requestId, String challenge,
                   Map<String,Object> context, String runId, long requestedAtNanos) {
        try {
            Map<String,Object> envelope=Json.object(response);
            String certificate=Json.string(envelope,"caller_context");
            Map<String,Object> caller=certificate(certificate);
            for(String binding:new String[]{"client_instance_id","runtime_plugin_id","build_sha256","development"}) {
                if(!context.containsKey(binding) || !Objects.equals(context.get(binding),caller.get(binding)))throw invalid();
            }
            String proof=Json.string(envelope,"proof"), resultText=Json.string(envelope,"result_json");
            if(proof.length()>16384 || resultText.getBytes(StandardCharsets.UTF_8).length>1048576) throw invalid();
            String[] parts=proof.split("\\.",-1); if(parts.length!=3)throw invalid();
            Map<String,Object> header=Json.object(utf8(decode(parts[0])));
            if(!"RS256".equals(header.get("alg")) || !"titan-authorization+jwt".equals(header.get("typ")))throw invalid();
            for(String forbidden:new String[]{"crit","jku","jwk","x5u","x5c","b64"})if(header.containsKey(forbidden))throw invalid();
            String key=keys.get(Json.string(header,"kid")); if(key==null)throw invalid();
            PublicKey publicKey=KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key)));
            Signature signature=Signature.getInstance("SHA256withRSA"); signature.initVerify(publicKey);
            signature.update((parts[0]+"."+parts[1]).getBytes(StandardCharsets.US_ASCII));
            if(!signature.verify(decode(parts[2])))throw invalid();
            Map<String,Object> claims=Json.object(utf8(decode(parts[1])));
            long issued=Json.number(claims,"iat"), expires=Json.number(claims,"exp"), notBefore=Json.number(claims,"nbf");
            long now=wallSeconds.getAsLong();
            String purpose=operation.equals("check")||operation.equals("list")?"plugin_entitlements":"plugin_run";
            if(Json.number(claims,"v")!=2 || !"https://titan.lc".equals(claims.get("iss")) ||
               !"titan-authorization".equals(claims.get("aud")) || !purpose.equals(claims.get("purpose")) ||
               issued<=0 || expires<=issued || expires-issued>300 || notBefore>issued || issued>now+30 ||
               notBefore>now+30 || expires<=now || !operation.equals(claims.get("operation")) ||
               !requestId.equals(claims.get("request_id")) || !challenge.equals(claims.get("challenge")))throw invalid();
            for(String binding:new String[]{"client_instance_id","runtime_plugin_id","build_sha256","development"}) {
                if(!context.containsKey(binding) || !Objects.equals(context.get(binding),claims.get(binding)))throw invalid();
            }
            if(!generation(context.get("runtime_generation")).equals(generation(caller.get("runtime_generation"))) ||
               !generation(context.get("runtime_generation")).equals(generation(claims.get("runtime_generation"))))throw invalid();
            for(String binding:new String[]{"sub","did","dv","caller_plugin_id","canonical_plugin_id"})
                if(!caller.containsKey(binding)||!Objects.equals(caller.get(binding),claims.get(binding)))throw invalid();
            if(!hash(certificate).equals(claims.get("caller_context_sha256")))throw invalid();
            byte[] digest=MessageDigest.getInstance("SHA-256").digest(resultText.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex=new StringBuilder(); for(byte b:digest)hex.append(String.format("%02x",b&255));
            if(!hex.toString().equals(Json.string(claims,"result_sha256")))throw invalid();
            Map<String,Object> result=Json.object(resultText);
            if(!operation.equals(result.get("operation")))throw invalid();
            if(runId!=null&&!runId.equals(claims.get("run_id"))&&
                (claims.containsKey("run_id")||!Boolean.FALSE.equals(result.get("allowed"))))throw invalid();
            long deadline=requestedAtNanos+Math.min(expires-issued,expires-now)*1000000000L;
            if(deadline<=monotonicNanos.getAsLong())throw invalid();
            return new Receipt(claims,result,deadline);
        } catch(GeneralSecurityException | CharacterCodingException | IllegalArgumentException failure) {
            throw new IllegalArgumentException("invalid_authorization_receipt",failure);
        }
    }
    private Map<String,Object> certificate(String compact) throws GeneralSecurityException,CharacterCodingException {
        if(compact.length()>16384)throw invalid();String[] parts=compact.split("\\.",-1);if(parts.length!=3)throw invalid();
        Map<String,Object> header=Json.object(utf8(decode(parts[0])));
        if(!"RS256".equals(header.get("alg"))||!"titan-authorization+jwt".equals(header.get("typ")))throw invalid();
        for(String key:new String[]{"crit","jku","jwk","x5u","x5c","b64"})if(header.containsKey(key))throw invalid();
        String key=keys.get(Json.string(header,"kid"));if(key==null)throw invalid();
        Signature sig=Signature.getInstance("SHA256withRSA");
        sig.initVerify(KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(key))));
        sig.update((parts[0]+"."+parts[1]).getBytes(StandardCharsets.US_ASCII));if(!sig.verify(decode(parts[2])))throw invalid();
        Map<String,Object> c=Json.object(utf8(decode(parts[1])));
        long iat=Json.number(c,"iat"),exp=Json.number(c,"exp"),nbf=Json.number(c,"nbf"),now=wallSeconds.getAsLong();
        if(Json.number(c,"v")!=2||!"https://titan.lc".equals(c.get("iss"))||!"titan-authorization".equals(c.get("aud"))||
           !"plugin_runtime_context".equals(c.get("purpose"))||iat<=0||exp<=iat||exp-iat>1800||nbf>iat||iat>now+30||nbf>now+30||exp<=now)throw invalid();
        return c;
    }
    private static String hash(String text) throws GeneralSecurityException {
        StringBuilder hex=new StringBuilder();for(byte b:MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8)))hex.append(String.format("%02x",b&255));return hex.toString();
    }
    private static IllegalArgumentException invalid(){return new IllegalArgumentException("invalid_authorization_receipt");}
    private static String generation(Object value) {
        if(value instanceof Long && (Long)value>0)return value.toString();
        if(value instanceof String && ((String)value).matches("[1-9][0-9]{0,15}")){
            long parsed=Long.parseLong((String)value);if(parsed<=9007199254740991L)return (String)value;
        }
        throw invalid();
    }
    private static byte[] decode(String value) {
        if(!value.matches("[A-Za-z0-9_-]+"))throw invalid();
        byte[] decoded=Base64.getUrlDecoder().decode(value);
        if(!Base64.getUrlEncoder().withoutPadding().encodeToString(decoded).equals(value))throw invalid();
        return decoded;
    }
    private static String utf8(byte[] value) throws CharacterCodingException {
        return StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(value)).toString();
    }
}
