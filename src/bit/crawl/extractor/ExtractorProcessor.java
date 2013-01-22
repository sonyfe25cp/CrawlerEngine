package bit.crawl.extractor;

import bit.crawl.store.StoredPage;

public interface ExtractorProcessor {
	Object extract(StoredPage page);
}
