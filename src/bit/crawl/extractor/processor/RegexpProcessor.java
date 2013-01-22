package bit.crawl.extractor.processor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bit.crawl.extractor.ExtractorProcessor;
import bit.crawl.extractor.util.HTMLCleaner;
import bit.crawl.store.StoredPage;

public class RegexpProcessor implements ExtractorProcessor {

	Map<String, Pattern> patterns = new LinkedHashMap<String, Pattern>();

	public Map<String, Pattern> getPatterns() {
		return patterns;
	}

	public void setPatterns(Map<String, Pattern> patterns) {
		this.patterns = patterns;
	}

	@Override
	public Object extract(StoredPage page) {
		Map<String, String> results = new LinkedHashMap<String, String>();
		results.put("URL", page.getHeader("URL"));
		
		for(Map.Entry<String, Pattern> np : patterns.entrySet()) {
			Matcher matcher = np.getValue().matcher(page.getContent());
			if (matcher.find()) {
				String matched = matcher.group(1);
				String cleaned = HTMLCleaner.cleanup(matched);
				results.put(np.getKey(), cleaned);
			} else {
				results.put(np.getKey(), null);
			}
		}
		
		return results;
	}

}
