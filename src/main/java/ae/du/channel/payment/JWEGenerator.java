package ae.du.channel.payment;

import com.cybersource.flex.android.CaptureContext;
import decompiled.cybersource.flex.android.jwt.JWE;
import decompiled.cybersource.flex.android.jwt.JWS;
import decompiled.cybersource.flex.android.jwt.JWT;
import decompiled.cybersource.flex.android.util.LongTermKey;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWEGenerator {

    public static void main(String[] args) {

        final String sessionResponse = "eyJraWQiOiJ6dSIsImFsZyI6IlJTMjU2In0.eyJmbHgiOnsicGF0aCI6Ii9mbGV4L3YyL3Rva2VucyIsImRhdGEiOiJmMTg1OGJmYzVsTlZ4d2ZSVHhlWlpSQUFFQWFYODZta0VFdnlSWVRNOU1OdHhRRE5XRG1sU3cxYTJuK2piSFhtRU05OC9EdGFSV2ZoRmZ4Q2RiVkZmMkFybGZaNjIvQUV0YW5oU2VZcG0xM2FxN3NaWFROUEg0dXZaMEdKaGFmaDlMTXUiLCJvcmlnaW4iOiJodHRwczovL3Rlc3RmbGV4LmN5YmVyc291cmNlLmNvbSIsImp3ayI6eyJrdHkiOiJSU0EiLCJlIjoiQVFBQiIsInVzZSI6ImVuYyIsIm4iOiJwZXRpcmdOTVRNeXhtTzVMLWJXOUowX3c1WHpMSFU0cm80NnNyNTRSeDF6UDFad2JMR25Xa1lZNEhtX01mN0pxb2MwbEU0S2hkX3lDY2VRWnNxdTZMaUZpZmlNNG9pYW1zVHhqbTBtYjhKMF9LR1l6UzNjOU11MFVzT2xZOXZzVk9QaGpwX1Q1MURtd25QZngyLUVXdHJFb3JGa1plRjNkbFlfRmtqVFdud0YySDZ4RzJERXJmQUJ3RWVWeWlPUlRlMTBCeUl5bzY4WDAzdkJvTGhham1SVFhidFFCZnQ2WUdHanozcm9TTzh1UV9pR3FfQmQ0RFg4RHZCRnlNQV9tYnBHbnN6YW1OanR5M0ZZTmMxVU1RLWd0YmRobkRtdnVrNl9yQW05alN6RFM0RFp1QS1NU0RaZnZueEtHd2JnRm5GSnRIVFF3LTJOUjFtX1NScmxnUVEiLCJraWQiOiIwOENRMHh5d0RGUTFKbDFRNlU4V0ZsaVlSUkhTWU9wYyJ9fSwiY3R4IjpbeyJkYXRhIjp7InJlcXVpcmVkRmllbGRzIjpbInBheW1lbnRJbmZvcm1hdGlvbi5jYXJkLm51bWJlciJdLCJvcHRpb25hbEZpZWxkcyI6WyJwYXltZW50SW5mb3JtYXRpb24uY2FyZC5leHBpcmF0aW9uWWVhciIsInBheW1lbnRJbmZvcm1hdGlvbi5jYXJkLmV4cGlyYXRpb25Nb250aCIsInBheW1lbnRJbmZvcm1hdGlvbi5jYXJkLnR5cGUiLCJwYXltZW50SW5mb3JtYXRpb24uY2FyZC5zZWN1cml0eUNvZGUiXX0sInR5cGUiOiJhcGktMC4xLjAifV0sImlzcyI6IkZsZXggQVBJIiwiZXhwIjoxNjQzMDI0Nzc3LCJpYXQiOjE2NDMwMjM4NzcsImp0aSI6ImdNc0hIUE1BU09MVXRGb0cifQ.BZpumxQI_Hu5v_BQb1uWth_bQJeAuw1HMJ-QLJgJ7apuDMoVrVARKnY7Ba3zm9j72C93N-ewjVloQwpTr3P2JmQoiLfcvuHu9pYEO6QdWe2sq4bz7gG8fjgwwu0VyjMjeYcZoRCJF8Ijy8erHqB_-tv1xiuyZyS0RuxdbQOvuDLJsM50qRwALse72t2asva-2aAVhoeVFQGGXtBUEom--U5DzBwJj6QsGyt64Kc8jj7mwjCZpttrAda7XoiIknAqhCle7PFE_dd8w_Du2P2b83Tesp5FyzydqvwWbWrY_2uWMEBwb5ysQQuv-3QPANrzekWKn4QJn-u58tig-25eYA";
        final CaptureContext context = new CaptureContextImpl(sessionResponse);
        System.out.println(context.jwe(dummyCapturedCard()));
    }

    private static Map<String, Object> dummyCapturedCard() {
        final Map<String, Object> card = new HashMap<>();
        card.put("paymentInformation.card.number", "4847897632456743");
        card.put("paymentInformation.card.expirationMonth", "05");
        card.put("paymentInformation.card.expirationYear", "22");
        card.put("paymentInformation.card.securityCode", "444");

        return card;
    }
}

class CaptureContextImpl implements CaptureContext {

    private final JWT jwt;
    private final String src;

    public CaptureContextImpl(final String jwt) {
        this.src = jwt;
        this.jwt = JWS.parse(jwt, LongTermKey.jwtValidationKey(jwt));
    }

    @Override
    public final String getId() {
        return jwt.payload("jti");
    }

    @Override
    public Date getExpiry() {
        return new Date((long) jwt.payload("exp") * 1000L);
    }

    @Override
    public Date getCreated() {
        return new Date((long) jwt.payload("iat") * 1000L);
    }

    @Override
    public PublicKey getPublicKey() {
        try {
            final Map<String, String> flexPublicKey = getJsonWebKey();
            final RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(
                    new BigInteger(1, Base64.getUrlDecoder().decode(flexPublicKey.get("n"))),
                    new BigInteger(1, Base64.getUrlDecoder().decode(flexPublicKey.get("e")))
            );
            final KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePublic(publicKeySpec);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unable to decode public RSA key", e);
        }
    }

    @Override
    public Map<String, String> getJsonWebKey() {
        final Map<String, Object> flx = jwt.payload("flx");
        return (Map<String, String>) flx.get("jwk");
    }

    @Override
    public String jwe(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("At least one key/value pair must be provided.");
        }

        if (getExpiry().before(new Date())) {
            throw new IllegalArgumentException("Capture Context has expired, please fetch a new one.");
        }

        final JWE jwe = new JWE();
        jwe.payload("context", toString());
        jwe.payload("data", data);
        jwe.payload("index", 0L);
        return jwe.compact(getJsonWebKey().get("kid"), getPublicKey());
    }

    @Override
    public final String toString() {
        return src;
    }

    public String getFlexOrigin() {
        final Map<String, Object> flx = jwt.payload("flx");
        return flx.get("origin").toString();
    }

    public String getTokensPath() {
        final Map<String, Object> flx = jwt.payload("flx");
        return flx.get("path").toString();
    }

    @Override
    public Map<String, Object> ctx(String type) {
        final Object[] ctx = jwt.payload("ctx");
        for (Object e : ctx) {
            Map<String, Object> map = (Map<String, Object>) e;
            if (type.equals(map.get("type")))
                return map;
        }
        return null;
    }
}

