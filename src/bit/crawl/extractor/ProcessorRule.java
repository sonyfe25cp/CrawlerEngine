package bit.crawl.extractor;

import bit.crawl.store.StoredPage;

public interface ProcessorRule {
	boolean isSuitableFor(StoredPage storedPage);
	ExtractorProcessor getProcessor();
	String getDbName();
}
