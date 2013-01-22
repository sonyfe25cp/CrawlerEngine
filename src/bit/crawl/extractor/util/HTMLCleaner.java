package bit.crawl.extractor.util;

import java.util.regex.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class HTMLCleaner {

	private static final Pattern mkPat(String pat) {
		return Pattern.compile(pat, Pattern.CASE_INSENSITIVE
				| Pattern.UNICODE_CASE | Pattern.DOTALL);
	}

	private static final Pattern mkTagPat(String tag) {
		// Care should be taken for unterminated tags.
		return mkPat(String.format("<%s.*?>(.*?)(</%s>|$)", tag, tag));
	}

	public static final Pattern RE_COMMENT = mkPat("<!--.*?-->");
	
	public static final Pattern RE_TAG = mkPat("<.*?>");
	public static final Pattern RE_TITLE = mkTagPat("title");
	public static final Pattern RE_BODY = mkTagPat("body");
	public static final Pattern RE_SCRIPT = mkTagPat("script");
	public static final Pattern RE_STYLE = mkTagPat("style");
	public static final Pattern RE_WHITESPACES = mkPat("[ \t\u000b\f]+");
	public static final Pattern RE_NEWLINES = mkPat("[\n\r]+");
	public static final Pattern RE_EMPTY_LINES = mkPat("([ \t\u000b\f]*[\n\r])+");

	public static String removePattern(Pattern pattern, String content) {
		return StringUtils.join(pattern.split(content));
	}

	public static String extractPattern(Pattern pattern, String content, String def) {
		try {
			Matcher m = pattern.matcher(content);
			m.find();
			return m.group(1);
		} catch (IllegalStateException e) {
			return def;
		}
	}

	private static String removeComment(String content) {
		return removePattern(RE_COMMENT, content);
	}

	public static String removeAllTags(String content) {
		return removePattern(RE_TAG, content);
	}

	public static String removeStyles(String content) {
		return removePattern(RE_STYLE, content);
	}
	
	public static String removeScripts(String content) {
		return removePattern(RE_SCRIPT, content);
	}

	public static String keepBody(String content) {
		return extractPattern(RE_BODY, content, content);
	}

	public static String extractTitle(String content) {
		return extractPattern(RE_TITLE, content, "");
	}
	
	public static String squeezeSpaces(String content) {
		return StringUtils.join(RE_WHITESPACES.split(content), " ");
	}
	
	public static String squeezeNewLines(String content) {
		return StringUtils.join(RE_NEWLINES.split(content), "\n");
	}
	public static String squeezeEmptyLines(String content) {
		return StringUtils.join(RE_EMPTY_LINES.split(content), "\n");
	}

	public static String cleanup(String content) {
		String noComment = removeComment(content);
		String body = keepBody(noComment);

		body = removeScripts(body);
		body = removeStyles(body);

		body = removeAllTags(body);
		body = squeezeEmptyLines(body);
		body = squeezeSpaces(body);
		body = StringEscapeUtils.unescapeHtml(body);

		return body;
	}

	public static void main(String[] args) {
		String document = "<titl=e>heha</title>";
		String title = extractTitle(document);
		System.out.println(title);
	}

}
