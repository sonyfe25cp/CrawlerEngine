package bit.crawl.crawler;

import java.util.*;

/**
 * Saving informatins about a web page, including its URL, its full contents,
 * all links, etc.
 * 
 */
public class PageInfo {
	/**
	 * Distance from the initial URLs.
	 */
	private int distance;

	/**
	 * The URL.
	 */
	private String url = null;
	private String referURL = null;//the refer information of this url
	
	/**
	 * The HTTP headers.
	 */
	private Map<String, String> headers = new LinkedHashMap<String, String>();
	
	private Integer httpStatus;
	private String etag;
	private Date lastModified;
	
	private Date lastResponded;
	private Integer crawlStatus;
	
	private CrawlAction crawlFlag;//avoid to add distance when crawler just crawl the follow page
	
	
	/**
	 * The raw (un-decoded) contents.
	 */
	private byte[] rawContent = null;

	/**
	 * The full content (as unicode).
	 */
	private String content = null;

	/**
	 * All links. Note: This may include mailto:xxx@xxx.xxx or other urls than
	 * HTTP.
	 */
	private List<String> links = new ArrayList<String>();

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	public Integer getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(Integer httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getLastResponded() {
		return lastResponded;
	}

	public void setLastResponded(Date lastResponded) {
		this.lastResponded = lastResponded;
	}

	public Integer getCrawlStatus() {
		return crawlStatus;
	}

	public void setCrawlStatus(Integer crawlStatus) {
		this.crawlStatus = crawlStatus;
	}

	public byte[] getRawContent() {
		return rawContent;
	}

	public void setRawContent(byte[] rawContent) {
		this.rawContent = rawContent;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getLinks() {
		return links;
	}

	public void setLinks(List<String> links) {
		this.links = links;
	}

	public CrawlAction getCrawlFlag() {
		return crawlFlag;
	}

	public void setCrawlFlag(CrawlAction crawlFlag) {
		this.crawlFlag = crawlFlag;
	}

	public String getReferURL() {
		return referURL;
	}

	public void setReferURL(String referURL) {
		this.referURL = referURL;
	}

}