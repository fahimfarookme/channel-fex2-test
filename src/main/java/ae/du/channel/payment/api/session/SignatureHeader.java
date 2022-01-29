package ae.du.channel.payment.api.session;

import ae.du.channel.payment.Configuration;
import ae.du.channel.payment.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.stringtemplate.v4.ST;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class SignatureHeader {

    @Autowired
    private Configuration config;

    @Value("classpath:signature-header.txt")
    private Resource signatureHeaderFile;

    @Value("classpath:signature.txt")
    private Resource signatureFile;

    private final DigestHeader digest;

    public SignatureHeader(final DigestHeader digest) {
        this.digest = digest;
    }

    public String generate() {
        final ST st = new ST(Util.asString(this.signatureHeaderFile));
        st.add("keyid", this.config.getMerchant().getKeyId());
        st.add("algorithm", this.config.getMerchant().getAlgorithm());
        st.add("signature", signedSignature());

        return st.render();
    }

    private String signedSignature() {
        return signFrom(plainSignature(), this.config.getMerchant().getSecretKey());
    }

    private String plainSignature() {
        final ST st = new ST(Util.asString(this.signatureFile));
        st.add("host", this.config.getMerchant().getRequestHost());
//        st.add("date", "Sat, 29 Jan 2022 19:59:07 GMT");
        System.out.println(Util.newDate());
        st.add("date", Util.newDate());
        st.add("digest", this.digest.generate());
        st.add("merchantId", this.config.getMerchant().getId());
        return st.render();
    }

    private static String signFrom(final String text, final String secret) {
       assert text != null;
       assert secret != null;

        try {
            SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(secret), "HmacSHA256");
            Mac aKeyId = Mac.getInstance("HmacSHA256");
            aKeyId.init((Key)secretKey);
            aKeyId.update(text.getBytes(StandardCharsets.UTF_8));

            byte[] aHeaders = aKeyId.doFinal();
            String signedMessage = Base64.getEncoder().encodeToString(aHeaders);
            return signedMessage;
        } catch (final NoSuchAlgorithmException | InvalidKeyException e) {
            throw new SigningFailureException("Signature signing failed", e);
        }

    }

    public static final class SigningFailureException extends RuntimeException {
        public SigningFailureException(final String message, final Throwable t) {
            super(message, t);
        }
    }
}
