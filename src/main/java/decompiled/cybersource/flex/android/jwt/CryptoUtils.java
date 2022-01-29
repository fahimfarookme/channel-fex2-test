package decompiled.cybersource.flex.android.jwt;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CryptoUtils {

    private static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
    private static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";
    private static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
    private static final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";

    private CryptoUtils() {
        throw new IllegalStateException("This is a utility class.");
    }

    public static PrivateKey privateKey(String pem) {
        if (pem == null) {
            throw new IllegalArgumentException("pem is null");
        }

        pem = pem.substring(pem.indexOf(BEGIN_PRIVATE_KEY) + BEGIN_PRIVATE_KEY.length());
        pem = pem.substring(0, pem.indexOf(END_PRIVATE_KEY));
        pem = pem.trim();
        final byte[] keyMaterial = Base64.getMimeDecoder().decode(pem);
        return privateKey(keyMaterial);
    }

    public static PrivateKey privateKey(byte[] keyMaterial) {
        try {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyMaterial);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PublicKey publicKey(String pem) {
        if (pem == null) {
            throw new IllegalArgumentException("pem is null");
        }

        pem = pem.substring(pem.indexOf(BEGIN_PUBLIC_KEY) + BEGIN_PUBLIC_KEY.length());
        pem = pem.substring(0, pem.indexOf(END_PUBLIC_KEY));
        pem = pem.trim();
        final byte[] keyMaterial = Base64.getMimeDecoder().decode(pem);
        return publicKey(keyMaterial);
    }

    public static PublicKey publicKey(byte[] keyMaterial) {
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyMaterial);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PublicKey publicKey(String modulus, String exponent) {
        try {
            RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(exponent));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String pem(PublicKey key) {
        StringBuilder sb = new StringBuilder();
        sb.append(BEGIN_PUBLIC_KEY).append('\n');
        sb.append(Base64.getMimeEncoder().encodeToString(key.getEncoded())).append('\n');
        sb.append(END_PUBLIC_KEY).append('\n');
        return sb.toString();
    }

}
