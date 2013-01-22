package bit.crawl.crawler;

/**
 * Specifying how a URL should be treated.
 * <dl>
 * <dt>AVOID</dt>
 * <dd>This URL should not be fetched nor stored.</dd>
 * <dt>FOLLOW</dt>
 * <dd>This URL can be followed, but is not stored.</dd> 
 * <dt>STORE</dt>
 * <dd>This URL is both fetched and stored.</dd> 
 * </dl>
 * 
 * @author Kunshan Wang
 * 
 */
public enum CrawlAction {
	AVOID, FOLLOW, STORE,FOLLOW_STORE;
}
