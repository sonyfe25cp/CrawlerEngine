package bit.crawl.crawler;

/**
 * Configuration for a particular site.
 * @author Kunshan Wang
 *
 */
public class SiteConfig {
	/**
	 * The host name.
	 */
	private String host;
	
	/**
	 * Minimum time (in seconds) between two consecutive connections to this site.
	 */
	private int fetchDelay;

	public SiteConfig() {
		super();
	}

	public SiteConfig(String host, int fetchDelay) {
		super();
		this.host = host;
		this.fetchDelay = fetchDelay;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getFetchDelay() {
		return fetchDelay;
	}

	public void setFetchDelay(int fetchDelay) {
		this.fetchDelay = fetchDelay;
	}

}
