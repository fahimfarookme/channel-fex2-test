package ae.du.channel.payment.api.session;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ae.du.channel.payment.Configuration;
import ae.du.channel.payment.Util;

@Component
public class Headers {

	@Autowired
	private Configuration config;

	private final SignatureHeader signatureHeader;
	private final DigestHeader digestHeader;

	public Headers(final SignatureHeader signatureHeader, final DigestHeader digestHeader) {
		this.signatureHeader = signatureHeader;
		this.digestHeader = digestHeader;
	}

	public final Map<String, String> generate() {

		final Map<String, String> headers = new HashMap<>();
		headers.put("v-c-merchant-id", this.config.getMerchant().getId());
		headers.put("Accept", "application/jwt");
		headers.put("Content-Type", "application/json;charset=utf-8");
//		headers.put("Date", "Sat, 29 Jan 2022 19:59:07 GMT");
		System.out.println(Util.newDate());
		headers.put("Date", Util.newDate());
		headers.put("Host", this.config.getMerchant().getRequestHost());
		headers.put("Connection", "keep-alive");
		headers.put("User-Agent", "Android");
		headers.put("Signature", this.signatureHeader.generate());
		headers.put("Digest", this.digestHeader.generate());

		return headers;
	}
}