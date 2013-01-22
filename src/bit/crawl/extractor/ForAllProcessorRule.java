package bit.crawl.extractor;

import bit.crawl.store.StoredPage;

public class ForAllProcessorRule extends BaseProcessorRule {

	@Override
	public boolean isSuitableFor(StoredPage storedPage) {
		return true;
	}

}
