package bit.crawl.util;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.*;
import org.mozilla.universalchardet.UniversalDetector;

public class EncodingGuesser {
	private static Log logger = LogFactory.getLog(EncodingGuesser.class);

	public static final Charset ISO88591CHARSET = Charset.forName("ISO-8859-1");
	public static final Charset UTF8CHARSET = Charset.forName("UTF-8");

	private static final Pattern[] charsetPatterns = new Pattern[] {
			Pattern.compile(
					"<meta.*?charset\\s*=\\s*['\"]?([a-zA-Z0-9_\\-]+)['\"]?.*?>",
					Pattern.CASE_INSENSITIVE),
			Pattern.compile(
					"<\\?xml.*?encoding\\s*=\\s*['\"]?([a-zA-Z0-9_\\-]+)['\"]?.*?\\?>",
					Pattern.CASE_INSENSITIVE), };

	private static final Pattern headerCharsetPattern = Pattern
			.compile("charset=([a-zA-Z0-9_\\-]+)");

	public static Charset guessFromMetaTag(byte[] rawContent) {
		String simpleDecode = new String(rawContent, ISO88591CHARSET);

		Charset pageCharset = null;

		for (Pattern charsetPattern : charsetPatterns) {
			Matcher m = charsetPattern.matcher(simpleDecode);
			boolean found = m.find();
			if (found) {
				String pageCharsetName = m.group(1);
				logger.debug(String.format(
						"Charset pattern found in HTML. charset is [%s]",
						pageCharsetName));
				try {
					pageCharset = Charset.forName(pageCharsetName);
				} catch (Exception e) {

				}
			}
		}

		return pageCharset;
	}

	public static Charset guessByJUniversalCharDet(byte[] content) {
		UniversalDetector detector = new UniversalDetector(null);
		detector.handleData(content, 0, content.length);
		detector.dataEnd();
		String charsetName = detector.getDetectedCharset();
		Charset charset = null;
		try {
			charset = Charset.forName(charsetName);
		} catch (Exception e) {
		}
		return charset;
	}

	public static Charset guessFromContentType(String contentType) {
		Charset pageCharset = null;
		Matcher m = headerCharsetPattern.matcher(contentType);
		boolean found = m.find();
		if (found) {
			String headerCharsetName = m.group(1);
			logger.debug(String.format(
					"Charset pattern found in header. charset is [%s]",
					headerCharsetName));
			pageCharset = Charset.forName(headerCharsetName);
		}
		return pageCharset;
	}

	public static Charset guessWithContentType(byte[] content,
			String contentType, Charset defaultCharset) {
		Charset charset = guessFromMetaTag(content);
		if (charset == null) {
			charset = guessByJUniversalCharDet(content);
		}
		if (charset == null) {
			charset = guessFromContentType(contentType);
		}
		if (charset == null) {
			charset = defaultCharset;
		}
		return charset;
	}

	public static Charset guessWithEncodingName(byte[] content,
			String encodingName, Charset defaultCharset) {
		Charset charset = guessFromMetaTag(content);
		if (charset == null) {
			charset = guessByJUniversalCharDet(content);
		}
		if (charset == null) {
			try {
				charset = Charset.forName(encodingName);
			} catch (Exception e) {
				// Ignore and use default.
			}
		}
		if (charset == null) {
			charset = defaultCharset;
		}
		return charset;
	}
}
