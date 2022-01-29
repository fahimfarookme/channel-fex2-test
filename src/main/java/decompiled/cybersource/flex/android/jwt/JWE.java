package decompiled.cybersource.flex.android.jwt;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Map;

public final class JWE extends JWT {

    /**
     * Default constructor with VISA preferred settings
     */
    public JWE() {
        this(Algorithm.RSAOAEP, Algorithm.A256GCM);
    }

    /**
     * @param alg Cryptographic Algorithms for Key Management ("alg" claim)
     * @param enc Cryptographic Algorithms for Content Encryption ("enc" claim)
     */
    public JWE(Algorithm alg, Algorithm enc) {
        if (alg != Algorithm.RSAOAEP && alg != Algorithm.RSAOAEP256) {
            throw new IllegalArgumentException("Please provide public key encryption algorithm.");
        }
        if (enc != Algorithm.A128GCM && enc != Algorithm.A256GCM) {
            throw new IllegalArgumentException("Please provide symmetric encryption algorithm.");
        }

        header("alg", alg.jwa);
        header("enc", enc.jwa);
    }

    public String compact(String kid, PublicKey publicKey) {
        header("kid", kid);
        final Algorithm alg = Algorithm.match(header("alg"));
        final Algorithm enc = Algorithm.match(header("enc"));
        final StringBuilder jwt = new StringBuilder();

        // BASE64URL(UTF8(JWE Protected Header))
        final String header = Tools.base64(header());
        jwt.append(header).append(Tools.DOT);

        // BASE64URL(JWE Encrypted Key)
        final byte[] aesKey = enc.aesKey();
        final byte[] encryptedKey = alg.encrypt(publicKey, aesKey);
        jwt.append(Tools.ENCODER.encodeToString(encryptedKey)).append(Tools.DOT);

        // BASE64URL(JWE Initialization Vector)
        final byte[] iv = Algorithm.nonce(96 / 8); // NIST recommendation
        jwt.append(Tools.ENCODER.encodeToString(iv)).append(Tools.DOT);

        // BASE64URL(JWE Ciphertext)
        final byte[] plainText = Tools.bytesUTF8(payload());
        final byte[] cipherTextWithTag = enc.encrypt(aesKey, iv, plainText, Tools.bytesASCII(header));
        final byte[] cipherText = Arrays.copyOfRange(cipherTextWithTag, 0, cipherTextWithTag.length - 128 / 8);
        jwt.append(Tools.ENCODER.encodeToString(cipherText)).append(Tools.DOT);

        // BASE64URL(JWE Authentication Tag)
        final byte[] authenticationTag = Arrays.copyOfRange(cipherTextWithTag, cipherTextWithTag.length - 128 / 8, cipherTextWithTag.length);
        jwt.append(Tools.ENCODER.encodeToString(authenticationTag));

        return jwt.toString();
    }

    public static JWE parse(String token, PrivateKey privateKey) {
        final Object[] parts = Tools.splitToken(token); // token is splittable
        final Map<String, Object> header = (Map<String, Object>) new Tools.JsonReader().read(parts[0].toString());
        final Algorithm alg = Algorithm.match(header.get("alg").toString()); // is supported alg?
        final Algorithm enc = Algorithm.match(header.get("enc").toString()); // is supported alg?
        final JWE jwe = new JWE(alg, enc); // are JWE algorithms?

        final byte[] aesKey = alg.decrypt(privateKey, (byte[]) parts[1]);
        final byte[] iv = (byte[]) parts[2];

        final byte[] cipherTextWithTag = new byte[((byte[]) parts[3]).length + ((byte[]) parts[4]).length];
        System.arraycopy((byte[]) parts[3], 0, cipherTextWithTag, 0, ((byte[]) parts[3]).length);
        System.arraycopy((byte[]) parts[4], 0, cipherTextWithTag, ((byte[]) parts[3]).length, ((byte[]) parts[4]).length);
        final byte[] plainText = enc.decrypt(aesKey, iv, cipherTextWithTag, Tools.bytesASCII(token.substring(0, token.indexOf('.'))));

        for (Map.Entry<String, Object> e : header.entrySet()) {
            jwe.header(e.getKey(), e.getValue().toString());
        }
        final Map<String, Object> payload = (Map<String, Object>) new Tools.JsonReader().read(new String(plainText, Tools.UTF8));
        jwe.payload(payload);
        return jwe;
    }

}
