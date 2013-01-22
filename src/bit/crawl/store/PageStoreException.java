package bit.crawl.store;

/**
 * Thrown if something goes wrong when storing pages.
 * 
 * @author Kunshan Wang
 * 
 */
public class PageStoreException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PageStoreException() {
		super();
	}

	public PageStoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public PageStoreException(String message) {
		super(message);
	}

	public PageStoreException(Throwable cause) {
		super(cause);
	}

}
