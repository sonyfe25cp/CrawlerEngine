package bit.crawl.crawler;

import java.util.*;
import java.nio.charset.Charset;
import java.util.concurrent.*;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import bit.crawl.crawler.impl.FetchJob;
import bit.crawl.crawler.impl.ICrawlerForWorker;
import bit.crawl.util.Logger;
import bit.crawl.util.ZeroLatch;

/**
 * Crawl web pages from an initial URL, follow links and save user-specified
 * pages.
 * 
 * @author Kunshan Wang
 * 
 */
public class Crawler implements Runnable, ICrawlerForWorker {
	private static Logger logger = new Logger();

	private ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager();
	private HttpClient httpClient = new DefaultHttpClient(connManager);

	/**
	 * The max number of theads to use.
	 */
	private int maxThreads;

	/**
	 * The max distance (in hops) of any crawlable pages from the initial pages.
	 */
	private int maxDepth;

	/**
	 * The encoding of the crawled pages.
	 */
	private Charset charset;

	/**
	 * Initial URLs to start crawl from.
	 */
	private List<String> initialUrls = new ArrayList<String>();

	/**
	 * Configure the site to crawl and restrict crawling inside this site.
	 */
	private List<SiteConfig> siteConfigs = new ArrayList<SiteConfig>();

	/**
	 * Filter rules.
	 */
	private List<FilterRule> filterRules = new ArrayList<FilterRule>();

	/**
	 * All objects that react to the pages to be stored.
	 */
	private List<IPageSaver> pageListeners = new ArrayList<IPageSaver>();

	/**
	 * Crawl history in database.
	 */
	private CrawlHistory crawlHistory = null;
	
	public CrawlHistory getCrawlHistory() {
		return crawlHistory;
	}

	public void setCrawlHistory(CrawlHistory crawlHistory) {
		this.crawlHistory = crawlHistory;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public int getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
		connManager.setMaxTotal(maxThreads);
		connManager.setDefaultMaxPerRoute(maxThreads);
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public String getEncoding() {
		return charset.name();
	}

	public void setEncoding(String encoding) {
		this.charset = Charset.forName(encoding);
	}

	public List<String> getInitialUrls() {
		return initialUrls;
	}

	public void setInitialUrls(List<String> initialUrls) {
		this.initialUrls = initialUrls;
	}

	public List<SiteConfig> getSiteConfigs() {
		return siteConfigs;
	}

	public void setSiteConfigs(List<SiteConfig> siteConfigs) {
		this.siteConfigs = siteConfigs;
	}

	public List<FilterRule> getFilterRules() {
		return filterRules;
	}

	public void setFilterRules(List<FilterRule> filterRules) {
		this.filterRules = filterRules;
	}

	public List<IPageSaver> getPageListeners() {
		return pageListeners;
	}

	public void setPageListeners(List<IPageSaver> pageListeners) {
		this.pageListeners = pageListeners;
	}

	/**
	 * A bean-style empty constructor.
	 * 
	 * @author Kunshan Wang
	 */
	public Crawler() {
	}

	/* Private fields */

	private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
	private Set<String> dispatched = new HashSet<String>();//抓取过的url

	private ThreadPoolExecutor executor = null;
	private ZeroLatch latch = new ZeroLatch(0);

	/**
	 * Add a PageListener to the internal listener list.
	 * 
	 * @author Kunshan Wang
	 * 
	 * @param listeer
	 *            The PageListener to add;
	 */
	public void addPageListener(IPageSaver listener) {
		pageListeners.add(listener);
	}

	/**
	 * Start crawling, save pages in pageInfos. It will return when all
	 * reachable pages are crawled.
	 * 
	 * @author Kunshan Wang
	 */
	public void run() {
		try {
			logger.info("Start crawling.");
			logger.info("Default charset:" + getEncoding());
			logger.info("Loading crawl history");
			if(crawlHistory!=null){
				crawlHistory.loadHistory();
			}
			executor = new ThreadPoolExecutor(getMaxThreads(), getMaxThreads(),
					0, TimeUnit.SECONDS, workQueue);

			reportLinks(getInitialUrls(), 0);
			
			latch.await();
			if(crawlHistory!=null){
				if(!crawlHistory.getBufferSet().isEmpty()){
					crawlHistory.addHistory();
				}
			}

		} catch (InterruptedException e) {
			logger.info("Crawling interrupted.");
			// Just let this thread be interrupted. Clean up in the finally
			// clause.
		} finally {
			// The current thread may be interrupted. If that happens, we should
			// terminate all threads in the executor.
			if (executor != null) {
				executor.shutdownNow();
			}
		}
		logger.info("Crawling stopped.");

	}

	private CrawlAction getFilterAction(String url) {
		CrawlAction action=null;
		for (FilterRule fr : getFilterRules()) {
			action = fr.judge(url);
			if (action != null) {
				return action;
			}
		}
		logger.info("url:"+url+" --- action:"+(action==null?"Follow":action));
		return CrawlAction.FOLLOW;
	}

	@Override
	public void reportPageFetched(PageInfo pageInfo) {
		String url = pageInfo.getUrl();
		if (getFilterAction(url) == CrawlAction.STORE) {
			//if map not contains url save it
			logger.info(String.format("Save page: %s", url));
			for (IPageSaver pageListener : pageListeners) {
				try {
					pageListener.savePage(pageInfo);
				} catch (Exception e) {
					logger.error("PageListener error", e);
				}
			}
			if(crawlHistory!=null){
				if(crawlHistory.getBufferSet().size() >= crawlHistory.getBufferSize()) {
					synchronized(crawlHistory.getBufferSet()) {
						crawlHistory.addHistory();
					}
				}
				else{
					crawlHistory.addToBufferSet(url);
				}
			}
		}
	}

	@Override
	public void reportLinks(List<String> urls, int newDistance) {
		if (newDistance >= getMaxDepth()) {
			logger.debug("Distance %d too far away.  Do not add.", newDistance);
			return;
		}

		synchronized (dispatched) {
			for (String link : urls) {
				if (dispatched.contains(link)) {//已抓过??
					logger.debug(String.format("Discard dispatched link %s",
							link));
					continue;
				}
				if(crawlHistory!=null){
					if(crawlHistory.getHistorySet().contains(link)) {
						logger.info("some day already crawled this link:"+link);
						continue;
					}
				}
				if (getFilterAction(link) == CrawlAction.AVOID) {
					logger.debug(String.format("Discard AVOID link %s", link));
					continue;
				}

				logger.debug(String.format("Add link %s", link));
				latch.countUp();
				dispatched.add(link);
				PageInfo pageInfo = new PageInfo();
				pageInfo.setUrl(link);
				pageInfo.setDistance(newDistance);
				pageInfo.setCrawlFlag(getFilterAction(link));
				try {
					executor.execute(new FetchJob(httpClient, pageInfo, this));
				} catch (RejectedExecutionException e) {
					// executor is shutdown. do nothing.
				}
			}
		}
	}

	@Override
	public void reportJobDone() {
		latch.countDown();
	}

	public void stop() {
		if (executor != null) {
			executor.shutdownNow();
			latch.forceRelease();
		}
	}
}
