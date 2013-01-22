package bit.crawl.server;

public class CrawlServerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CrawlServerException() {
	}

	public CrawlServerException(String message) {
		super(message);
	}

	public CrawlServerException(Throwable cause) {
		super(cause);
	}

	public CrawlServerException(String message, Throwable cause) {
		super(message, cause);
	}

}
