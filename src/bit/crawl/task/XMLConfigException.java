package bit.crawl.task;

/**
 * Thrown when an error occur during loading an XML crawl task config file.
 * 
 * @author Kunshan Wang
 */
public class XMLConfigException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public XMLConfigException() {
		super();
	}

	public XMLConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public XMLConfigException(String message) {
		super(message);
	}

	public XMLConfigException(Throwable cause) {
		super(cause);
	}
	
	
}
