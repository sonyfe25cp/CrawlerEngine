package bit.crawl.extractor;

import bit.crawl.store.StoredPage;

public interface ExtractorSource {
	StoredPage readPage();
}
