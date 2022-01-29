package decompiled.cybersource.flex.android.jwt;

import java.security.Key;
import java.util.Map;

public final class JWS extends JWT {

    /**
     * Default constructor with VISA preferred settings
     */
    public JWS() {
        this(Algorithm.RSA256);
    }

    /**
     * @param alg Cryptographic Algorithms for Digital Signatures and MACs ("alg" claim)
     */
    public JWS(Algorithm alg) {
        if (alg != Algorithm.HS256 && alg != Algorithm.RSA256 && alg != Algorithm.RSA512) {
            throw new IllegalArgumentException("Please provide signing algorithm.");
        }

        header("alg", alg.jwa);
    }

    public static JWS parse(String token, Key key) {
        final Object[] parts = Tools.splitToken(token); // token is splittable
        final Map<String, Object> header = (Map<String, Object>) new Tools.JsonReader().read(parts[0].toString());
        final Algorithm alg = Algorithm.match(header.get("alg").toString()); // is supported alg?
        final JWS jws = new JWS(alg); // is JWS alg?

        final boolean verified = alg.verify(key, token.substring(0, token.lastIndexOf('.')), (byte[]) parts[2]);
        if (!verified) {
            throw new IllegalArgumentException("Invalid signature");
        }

        for(Map.Entry<String, Object> e : header.entrySet()) {
            jws.header(e.getKey(), e.getValue().toString());
        }
        final Map<String, Object> payload = (Map<String, Object>) new Tools.JsonReader().read(parts[1].toString());
        jws.payload(payload);
        return jws;
    }

    public String compact(String kid, Key privateKey) {
        if (kid != null) {
            header("kid", kid);
        }

        final Algorithm algorithm = Algorithm.match(header("alg"));
        final StringBuilder jwt = new StringBuilder();

        jwt.append(Tools.base64(header()));
        jwt.append(Tools.DOT);
        jwt.append(Tools.base64(payload()));

        final byte[] signature = algorithm.sign(privateKey, jwt.toString());
        jwt.append('.');
        jwt.append(Tools.ENCODER.encodeToString(signature));
        return jwt.toString();
    }

}
