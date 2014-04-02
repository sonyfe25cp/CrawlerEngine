package bit.crawl.task;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import bit.crawl.crawler.Crawler;
import bit.crawl.crawler.FilterRule;
import bit.crawl.crawler.IPageSaver;
import bit.crawl.crawler.PageInfo;
import bit.crawl.crawler.SiteConfig;
import bit.crawl.store.PageStoreWriter;
import bit.crawl.store.StoredPage;
import bit.crawl.util.Logger;

/**
 * A crawling task.
 * 
 * @author Kunshan Wang
 * 
 */
public class CrawlTask implements Runnable, IPageSaver {
	private static Logger logger = new Logger();

	private String taskName;
	private String baseDir;

	private String storageFileName;

	/**
	 * The crawler associated to this task.
	 */
	private Crawler crawler = new Crawler();

	/**
	 * The associated PageStore
	 */
	private PageStoreWriter pageStoreWriter = null;

	/* BEGIN ** Getters and setters */

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public String getStorageFileName() {
		return storageFileName;
	}

	public void setStorageFileName(String storageFileName) {
		this.storageFileName = storageFileName;
	}

	public Crawler getCrawler() {
		return crawler;
	}

	public void setCrawler(Crawler crawler) {
		this.crawler = crawler;
	}

	public PageStoreWriter getPageStoreWriter() {
		return pageStoreWriter;
	}

	public void setPageStoreWriter(PageStoreWriter pageStoreWriter) {
		this.pageStoreWriter = pageStoreWriter;
	}

	/* END ** Getters and setters */

	/* BEGIN ** Delegated properties from crawler */

	public int getMaxDepth() {
		return crawler.getMaxDepth();
	}

	public void setMaxDepth(int maxDepth) {
		crawler.setMaxDepth(maxDepth);
	}

	public String getEncoding() {
		return crawler.getEncoding();
	}

	public void setEncoding(String encoding) {
		crawler.setEncoding(encoding);
	}

	public List<String> getInitialUrls() {
		return crawler.getInitialUrls();
	}

	public void setInitialUrls(List<String> initialUrls) {
		crawler.setInitialUrls(initialUrls);
	}

	public List<SiteConfig> getSiteConfigs() {
		return crawler.getSiteConfigs();
	}

	public void setSiteConfigs(List<SiteConfig> siteConfigs) {
		crawler.setSiteConfigs(siteConfigs);
	}

	public List<FilterRule> getFilterRules() {
		return crawler.getFilterRules();
	}

	public void setFilterRules(List<FilterRule> filterRules) {
		crawler.setFilterRules(filterRules);
	}

	/* END ** Delegated properties from crawler */

	/**
	 * Run the crawling job.
	 * 
	 * @author Kunshan Wang
	 * 
	 */
	@Override
	public void run() {
		logger.debug(String.format("CrawlTask %s preparing crawler.", this));
		crawler.addPageListener(this);

		logger.debug(String.format("CrawlTask %s preparing pageStoreWriter.",
				this));

		String fileName = getStorageFileName();
		if (fileName == null) {
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			fileName = String.format("%s_%s.pages", getTaskName(),
					sdf.format(now));
		}

		pageStoreWriter = new PageStoreWriter(new File(getBaseDir(), fileName));

		logger.info(String.format("CrawlTask %s started.", this));
		crawler.run();
		pageStoreWriter.close();
		logger.info(String.format("CrawlTask %s finished.", this));
	}

	@Override
	public void savePage(PageInfo pageInfo) {
		StoredPage sp = new StoredPage();
		sp.setHeader("URL", pageInfo.getUrl().toString());
		sp.setContent(pageInfo.getContent());
		pageStoreWriter.store(sp);
	}

	public void stop() {
		crawler.stop();
	}
}
