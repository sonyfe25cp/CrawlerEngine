package bit.crawl.crawler.impl;

public class WontFetchException extends PageFetchException {

	private static final long serialVersionUID = -8750639919368961887L;

	public WontFetchException() {
	}

	public WontFetchException(String message, Throwable cause) {
		super(message, cause);
	}

	public WontFetchException(String message) {
		super(message);
	}

	public WontFetchException(Throwable cause) {
		super(cause);
	}

}
