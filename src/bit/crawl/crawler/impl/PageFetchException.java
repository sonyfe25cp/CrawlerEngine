package bit.crawl.crawler.impl;

import bit.crawl.crawler.CrawlerException;

/**
 * Thrown when errors occur during fetching pages from an URL or parsing pages.
 * 
 * @author Kunshan Wang
 * 
 */
public class PageFetchException extends CrawlerException {
	private static final long serialVersionUID = 1L;

	public PageFetchException() {
		super();
	}

	public PageFetchException(String message, Throwable cause) {
		super(message, cause);
	}

	public PageFetchException(String message) {
		super(message);
	}

	public PageFetchException(Throwable cause) {
		super(cause);
	}
}
