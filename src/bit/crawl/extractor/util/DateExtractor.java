package bit.crawl.extractor.util;

import java.util.regex.*;
import java.util.*;

public class DateExtractor {
	private static final Pattern DATE_PATTERN = Pattern
			.compile("(\\d{4})[年-](\\d{1,2})[月-](\\d{1,2})日?\\s*(?:(\\d{1,2}):(\\d{1,2})(?::(\\d{1,2}))?)?");

	private static int zeroable(String str) {
		if (str == null) {
			return 0;
		} else {
			return Integer.parseInt(str);
		}
	}
	public static Date extractDate(String content) {
		Matcher m = DATE_PATTERN.matcher(content);
		if(m.find()) {
			int year = Integer.parseInt(m.group(1));
			int month = Integer.parseInt(m.group(2));
			int day = Integer.parseInt(m.group(3));
			int hour = zeroable(m.group(4));
			int minute = zeroable(m.group(5));
			int second = zeroable(m.group(6));

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DAY_OF_MONTH, day);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, second);
			
			return cal.getTime();
			
		}
		return null;

	}
}
