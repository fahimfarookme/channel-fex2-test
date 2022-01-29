package ae.du.channel.payment.api.session;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DigestHeader {

	@Autowired
	private final Body body;

	public DigestHeader(final Body body) {
		this.body = body;
	}

	public String generate() {
		try {
			final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			;
			final byte[] digested = messageDigest.digest(this.body.generate().getBytes(StandardCharsets.UTF_8));
			return "SHA-256=" + Base64.getEncoder().encodeToString(digested);
		} catch (final NoSuchAlgorithmException e) {
			throw new DigestFailureException("Digest failed", e);
		}
	}

	public static final class DigestFailureException extends RuntimeException {
		public DigestFailureException(final String message, final Throwable t) {
			super(message, t);
		}
	}
}
