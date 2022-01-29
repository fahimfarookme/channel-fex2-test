package decompiled.cybersource.flex.android.jwt;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JWK {

    private final Map<String, Object> map = new HashMap<>();

    public JWK(final String keyId) {
        map.put("kid", keyId);
    }

    public static JWK from(final String kid, final PublicKey publicKey) {
        if ("RSA".equals(publicKey.getAlgorithm())) {
            final RSAPublicKey rsaKey = (RSAPublicKey) publicKey;
            final byte[] modulus = removeHeadingZeros(rsaKey.getModulus().toByteArray());
            final byte[] exponent = removeHeadingZeros(rsaKey.getPublicExponent().toByteArray());

            return new JWK(kid).setKeyType("RSA").setKeyUse("enc")
                    .setModulus(Tools.ENCODER.encodeToString(modulus))
                    .setExponent(Tools.ENCODER.encodeToString(exponent));
        }

        throw new IllegalArgumentException("Cannot process this publicKey");
    }

    private static byte[] removeHeadingZeros(byte[] bytes) {
        while (bytes.length > 0 && bytes[0] == 0x00) {
            bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
        }
        return bytes;
    }

    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(map);
    }

    public String getKeyType() {
        return map.get("kty").toString();
    }

    public JWK setKeyType(String keyType) {
        map.put("kty", keyType);
        return this;
    }

    public String getKeyUse() {
        return map.get("use").toString();
    }

    public JWK setKeyUse(String keyUse) {
        map.put("use", keyUse);
        return this;
    }

    public String getKeyId() {
        return map.get("kid").toString();
    }

    public String getModulus() {
        return map.get("n").toString();
    }

    public JWK setModulus(String modulus) {
        map.put("n", modulus);
        return this;
    }

    public String getExponent() {
        return map.get("e").toString();
    }

    public JWK setExponent(String exponent) {
        map.put("e", exponent);
        return this;
    }
}
