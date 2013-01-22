package bit.crawl.extractor;

import java.util.regex.Pattern;

import bit.crawl.store.StoredPage;

public class UrlProcessorRule extends BaseProcessorRule {

	private Pattern urlPattern;
	public Pattern getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(Pattern urlPattern) {
		this.urlPattern = urlPattern;
	}

	@Override
	public boolean isSuitableFor(StoredPage storedPage) {
		String url = storedPage.getHeader("URL");
		if (url==null) {
			return false;
		}
		return urlPattern.matcher(url).matches();
	}

}
