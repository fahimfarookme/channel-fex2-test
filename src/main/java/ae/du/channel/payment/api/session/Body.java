// CaptureContextHelper.java
package ae.du.channel.payment.api.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import ae.du.channel.payment.Util;

/**
 * Hints
 * https://stackoverflow.com/questions/3776923/how-can-i-normalize-the-eol-character-in-java
 * https://stackoverflow.com/questions/23829553/different-hash-value-created-on-windows-linux-and-mac-for-same-image
 */
@Component
public class Body {

	@Value("classpath:session-body.json")
	private Resource sessionBodyFile;

	public String generate() {
		final String body = Util.asString(this.sessionBodyFile);
		return body.replaceAll("\r\n", "\n");
	}
}