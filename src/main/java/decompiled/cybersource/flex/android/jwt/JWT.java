package decompiled.cybersource.flex.android.jwt;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class JWT implements Serializable {

    private final Map<String, Object> headerClaims = new HashMap<>();
    private final Map<String, Object> payloadClaims = new HashMap<>();

    protected JWT() {
        payload("jti", Tools.randomId(32));
        payload("iat", System.currentTimeMillis() / 1000L);
    }

    public Map<String, Object> header() {
        return Collections.unmodifiableMap(headerClaims);
    }

    public Map<String, Object> payload() {
        return Collections.unmodifiableMap(payloadClaims);
    }

    public String header(String key) {
        return headerClaims.get(key).toString();
    }

    public <T> T payload(String key) {
        return (T) payloadClaims.get(key);
    }

    public JWT header(String key, String val) {
        headerClaims.put(key, val);
        return this;
    }

    public JWT payload(Map<String, Object> map) {
        payloadClaims.putAll(map);
        return this;
    }

    public JWT payload(String key, Long val) {
        payloadClaims.put(key, val);
        return this;
    }

    public JWT payload(String key, Map<String, Object> val) {
        payloadClaims.put(key, val);
        return this;
    }

    public JWT payload(String key, String val) {
        payloadClaims.put(key, val);
        return this;
    }

    public String toJSON() {
        final StringBuilder json = new StringBuilder();
        Tools.write(json, payload());
        return json.toString();
    }

    public static Map<String, Object> parseHeader(String token) {
        final int dots = Tools.countDots(token);
        if (dots != 2 && dots != 4) {
            throw new IllegalArgumentException("Neither JWS nor JWE");
        }
        token = token.substring(0, token.indexOf('.'));
        token = new String(Tools.DECODER.decode(token), Tools.UTF8);

        Tools.JsonReader reader = new Tools.JsonReader();
        final Object json = reader.read(token);
        if (json instanceof Map) {
            return (Map<String, Object>) json;
        } else {
            throw new IllegalArgumentException("Not a JSON");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(": ").append(header()).append('.').append(payload());
        return sb.toString();
    }

}
