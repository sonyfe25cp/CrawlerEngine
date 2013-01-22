package bit.crawl.crawler;

/**
 * Thrown if something goes wrong during crawling.
 * 
 * @author Kunshan Wang
 * 
 */
public class CrawlerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CrawlerException() {
	}

	public CrawlerException(String message) {
		super(message);
	}

	public CrawlerException(Throwable cause) {
		super(cause);
	}

	public CrawlerException(String message, Throwable cause) {
		super(message, cause);
	}

}
