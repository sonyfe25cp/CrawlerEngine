package bit.crawl.extractor.processor;

import java.util.LinkedHashMap;

import bit.crawl.extractor.ExtractorProcessor;
import bit.crawl.store.StoredPage;

public class IdentityProcessor implements ExtractorProcessor {

	@Override
	public Object extract(StoredPage page) {
		LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>(
				page.getHeaders());
		m.put("content", page.getContent());

		return m;
	}

}
