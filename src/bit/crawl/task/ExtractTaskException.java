package bit.crawl.task;

public class ExtractTaskException extends RuntimeException {

	private static final long serialVersionUID = 8807799867081737089L;

	public ExtractTaskException() {
	}

	public ExtractTaskException(String message) {
		super(message);
	}

	public ExtractTaskException(Throwable cause) {
		super(cause);
	}

	public ExtractTaskException(String message, Throwable cause) {
		super(message, cause);
	}

}
