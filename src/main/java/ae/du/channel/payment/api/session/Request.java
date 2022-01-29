// CaptureContextHelper.java
package ae.du.channel.payment.api.session;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ae.du.channel.payment.Configuration;

@Component
public class Request {

	@Autowired
	private Configuration configs;

	@Autowired
	private final Headers headers;

	@Autowired
	private final Body body;

	@Autowired
	private final SessionCallback callback;

	private static final RestTemplate http = new RestTemplate();

	private static final Logger log = LoggerFactory.getLogger(SessionCallback.class);

	public Request(final Headers headers, final Body body, final SessionCallback callback) {
		this.headers = headers;
		this.body = body;
		this.callback = callback;
	}

	public final void call() {
		try {
			final String generatedBody = this.body.generate();
			log.debug("/session request body = {}", generatedBody);

			final Map<String, String> generatedHeaders = this.headers.generate();
			log.debug("/session request headers = {}", generatedHeaders);

			final ResponseEntity<String> response = http.postForEntity(
					this.configs.getMerchant().getSessionUrl(),
					new HttpEntity<>(generatedBody, convert(generatedHeaders)),
					String.class
			);
			log.debug("/session response = {}", response.getBody());

			if (response.getStatusCode() == HttpStatus.CREATED) {
				this.callback.success(response.getBody());
			} else {
				this.callback.failure(new Exception("Non 201 response"));
			}
		} catch (final Exception e) {
			log.error("/session failure", e);
			this.callback.failure(e);
		}
	}

	private HttpHeaders convert(final Map<String, String> map) {
		assert map != null;

		final HttpHeaders headers = new HttpHeaders();
		map.forEach((k, v) -> {
			headers.add(k, v);
		});

		return headers;
	}
}
