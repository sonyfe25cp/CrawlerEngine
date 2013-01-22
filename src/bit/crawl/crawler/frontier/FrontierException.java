package bit.crawl.crawler.frontier;

public class FrontierException extends RuntimeException {
	private static final long serialVersionUID = 7996033463381581064L;

	public FrontierException() {
	}

	public FrontierException(String message) {
		super(message);
	}

	public FrontierException(Throwable cause) {
		super(cause);
	}

	public FrontierException(String message, Throwable cause) {
		super(message, cause);
	}

}
