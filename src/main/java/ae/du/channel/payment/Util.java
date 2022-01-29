package ae.du.channel.payment;

import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public final class Util {

	private Util() {
		throw new UnsupportedOperationException("Utility class only");
	}

	public static String asString(final Resource resource) {
		assert resource != null;

		try (final Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
			return FileCopyUtils.copyToString(reader);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static String newDate() {
		return DateTimeFormatter.RFC_1123_DATE_TIME.format((TemporalAccessor) ZonedDateTime.now(ZoneId.of("GMT")));
	}
}
