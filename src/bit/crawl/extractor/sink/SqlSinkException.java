package bit.crawl.extractor.sink;

public class SqlSinkException extends RuntimeException {

	private static final long serialVersionUID = -8120402661925513144L;

	public SqlSinkException() {
	}

	public SqlSinkException(String message) {
		super(message);
	}

	public SqlSinkException(Throwable cause) {
		super(cause);
	}

	public SqlSinkException(String message, Throwable cause) {
		super(message, cause);
	}

}
