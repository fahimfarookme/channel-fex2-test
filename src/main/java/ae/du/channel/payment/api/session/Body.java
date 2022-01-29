// CaptureContextHelper.java
package ae.du.channel.payment.api.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import ae.du.channel.payment.Util;

@Component
public class Body {

	@Value("classpath:session-body.json")
	private Resource sessionBodyFile;

	public String generate() {
		final String body = Util.asString(this.sessionBodyFile);
		return body;
	}
}