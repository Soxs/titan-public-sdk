package net.titan.authorization;

import java.util.*;

/** Bounded, duplicate-rejecting JSON for signed protocol records. */
final class Json {
    private final String text;
    private int offset;
    private Json(String text) {
        if (text == null || text.length() > 1048576) throw new IllegalArgumentException("invalid_json_size");
        this.text = text;
    }
    static Object parse(String text) {
        Json p = new Json(text); Object value = p.value(0); p.space();
        if (p.offset != text.length()) throw p.bad();
        return value;
    }
    @SuppressWarnings("unchecked") static Map<String,Object> object(Object value) {
        if (!(value instanceof Map)) throw new IllegalArgumentException("expected_object");
        return (Map<String,Object>)value;
    }
    static Map<String,Object> object(String text) { return object(parse(text)); }
    static String string(Map<String,Object> map, String name) {
        Object value = map.get(name);
        if (!(value instanceof String)) throw new IllegalArgumentException("invalid_" + name);
        return (String)value;
    }
    static long number(Map<String,Object> map, String name) {
        Object value = map.get(name);
        if (!(value instanceof Long)) throw new IllegalArgumentException("invalid_" + name);
        return (Long)value;
    }
    static Map<String,Object> map(Object... pairs) {
        Map<String,Object> map = new LinkedHashMap<>();
        for (int i=0; i<pairs.length; i+=2) map.put((String)pairs[i],pairs[i+1]);
        return map;
    }
    private IllegalArgumentException bad() { return new IllegalArgumentException("invalid_json"); }
    private void space() { while(offset<text.length() && " \t\r\n".indexOf(text.charAt(offset))>=0) offset++; }
    private boolean take(char c) { space(); if(offset<text.length() && text.charAt(offset)==c){ offset++; return true; } return false; }
    private Object value(int depth) {
        if(depth>32) throw bad(); space(); if(offset>=text.length()) throw bad();
        char c=text.charAt(offset);
        if(c=='"') return string();
        if(take('{')) {
            Map<String,Object> map=new LinkedHashMap<>();
            if(take('}')) return Collections.unmodifiableMap(map);
            do {
                space(); if(offset>=text.length() || text.charAt(offset)!='"') throw bad();
                String key=string(); if(map.containsKey(key)||!take(':')) throw bad();
                map.put(key,value(depth+1));
                if(take('}')) return Collections.unmodifiableMap(map);
            } while(take(',')); throw bad();
        }
        if(take('[')) {
            List<Object> list=new ArrayList<>(); if(take(']')) return Collections.unmodifiableList(list);
            do { list.add(value(depth+1)); if(take(']')) return Collections.unmodifiableList(list); } while(take(',')); throw bad();
        }
        for(String literal:new String[]{"true","false","null"}) if(text.startsWith(literal,offset)) {
            offset+=literal.length(); return literal.equals("null")?null:literal.equals("true");
        }
        int start=offset; if(c=='-')offset++;
        if(offset>=text.length())throw bad();
        if(text.charAt(offset)=='0')offset++;
        else { if(text.charAt(offset)<'1'||text.charAt(offset)>'9')throw bad(); while(offset<text.length()&&text.charAt(offset)>='0'&&text.charAt(offset)<='9')offset++; }
        try { long n=Long.parseLong(text.substring(start,offset)); if(n < -9007199254740991L || n > 9007199254740991L)throw bad();return n; } catch(NumberFormatException e){throw bad();}
    }
    private String string() {
        offset++;StringBuilder out=new StringBuilder();
        while(offset<text.length()) {
            char c=text.charAt(offset++);
            if(c=='"') {
                String result=out.toString();
                for(int i=0;i<result.length();i++) {
                    char x=result.charAt(i);
                    if(Character.isHighSurrogate(x)){if(++i>=result.length()||!Character.isLowSurrogate(result.charAt(i)))throw bad();}
                    else if(Character.isLowSurrogate(x))throw bad();
                }
                return result;
            }
            if(c<32)throw bad();
            if(c=='\\') {
                if(offset>=text.length())throw bad();c=text.charAt(offset++);
                switch(c){case '"':case '\\':case '/':break;case 'b':c='\b';break;case 'f':c='\f';break;case 'n':c='\n';break;case 'r':c='\r';break;case 't':c='\t';break;
                case 'u':if(offset+4>text.length())throw bad();try{c=(char)Integer.parseInt(text.substring(offset,offset+4),16);}catch(NumberFormatException e){throw bad();}offset+=4;break;default:throw bad();}
            }
            out.append(c);
        }throw bad();
    }
    static String write(Object value) {
        if(value==null)return "null";
        if(value instanceof String){StringBuilder out=new StringBuilder("\"");for(char c:((String)value).toCharArray()){if(c=='"'||c=='\\')out.append('\\').append(c);else if(c<32)out.append(String.format("\\u%04x",(int)c));else out.append(c);}return out.append('"').toString();}
        if(value instanceof Boolean||value instanceof Number)return value.toString();
        if(value instanceof Map){StringJoiner join=new StringJoiner(",","{","}");((Map<?,?>)value).forEach((k,v)->join.add(write(k)+":"+write(v)));return join.toString();}
        if(value instanceof Iterable){StringJoiner join=new StringJoiner(",","[","]");for(Object v:(Iterable<?>)value)join.add(write(v));return join.toString();}
        throw new IllegalArgumentException("unsupported_json_value");
    }
}
