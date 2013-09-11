package bit.crawl.crawler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import bit.crawl.bloomfilter.BloomFilter;
import bit.crawl.crawler.impl.FetchJob;
import bit.crawl.crawler.impl.ICrawlerForWorker;
import bit.crawl.extractor.SimpleHtmlExtractor;
import bit.crawl.reporter.PDFReporter;
import bit.crawl.util.Logger;
import bit.crawl.util.ZeroLatch;

/**
 * Crawl web pages from an initial URL, follow links and save user-specified
 * pages.
 * 
 */
public class Crawler implements Runnable, ICrawlerForWorker {
	private static Logger logger = new Logger();

	private ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager();
	private HttpClient httpClient = new DefaultHttpClient(connManager);

	/**
	 * The max number of threads to use.
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

	/**
	 * Topic Crawl history in database.
	 */
	private CrawlHistory topicCrawlHistory = null;

	/**
	 * BloomFilter to check whether the element been added,if not add it
	 */
	private BloomFilter<String> bloomFilter = null;

	private boolean topicCrawler = false;
	private List<String> topicWords = new ArrayList<String>();
	private PDFReporter pdfReporter;
	private static int total;
	private static int topicSpecific;
	private static HashMap<String, String> pairs = new HashMap<String, String>();

	public PDFReporter getPdfReporter() {
		return pdfReporter;
	}

	public void setPdfReporter(PDFReporter pdfReporter) {
		this.pdfReporter = pdfReporter;
	}

	public boolean isTopicCrawler() {
		return topicCrawler;
	}

	public void setTopicCrawler(boolean topicCrawler) {
		this.topicCrawler = topicCrawler;
	}

	public CrawlHistory getTopicCrawlHistory() {
		return topicCrawlHistory;
	}

	public void setTopicCrawlHistory(CrawlHistory topicCrawlHistory) {
		this.topicCrawlHistory = topicCrawlHistory;
	}

	public CrawlHistory getCrawlHistory() {
		return crawlHistory;
	}

	public void setCrawlHistory(CrawlHistory crawlHistory) {
		this.crawlHistory = crawlHistory;
	}

	public BloomFilter<String> getBloomFilter() {
		return bloomFilter;
	}

	public void setBloomFilter(BloomFilter<String> bloomFilter) {
		this.bloomFilter = bloomFilter;
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

	public List<String> getTopicWords() {
		return topicWords;
	}

	public void setTopicWords(List<String> topicWords) {
		this.topicWords = topicWords;
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
	private Set<String> dispatched = new HashSet<String>();// 抓取过的url

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

			// 加载BloomFilter
			FileInputStream fis = null;
			try {
				fis = new FileInputStream("bloomFilter.bf");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(fis);
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				bloomFilter = (BloomFilter<String>) ois.readObject();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			// if(crawlHistory!=null){
			// crawlHistory.loadHistory();
			// }
			if (topicCrawler == true && topicCrawlHistory != null) {
				topicCrawlHistory.loadHistory();
			}
			executor = new ThreadPoolExecutor(getMaxThreads(), getMaxThreads(),
					0, TimeUnit.SECONDS, workQueue);

			reportLinks(getInitialUrls(), 0);

			latch.await();
			if (crawlHistory != null) {
				if (!crawlHistory.getBufferSet().isEmpty()) {
					crawlHistory.addHistory();
				}
			}
			if (topicCrawler == true && topicCrawlHistory != null) {
				if (!topicCrawlHistory.getBufferSet().isEmpty()) {
					topicCrawlHistory.addHistory();
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
		
		//序列化输出bloomFilter
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("bloomFilter.bf");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(fos);
			oos.writeObject(bloomFilter);
			oos.close();
			logger.info("bloomFilter serialize successfully!!!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		logger.info("Crawling stopped.");
		logger.info("Begin to generate the report.");
		pdfReporter.report(topicWords, total, topicSpecific, pairs);
		logger.info("Generete the report successfully.");
	}

	private CrawlAction getFilterAction(String url) {
		CrawlAction action = null;
		for (FilterRule fr : getFilterRules()) {
			action = fr.judge(url);
			if (action != null) {
				return action;
			}
		}
		logger.info("url:" + url + " --- action:"
				+ (action == null ? "Follow" : action));
		return CrawlAction.FOLLOW;
	}

	@Override
	public void reportPageFetched(PageInfo pageInfo) {
		String url = pageInfo.getUrl();
		total++;
		if (getFilterAction(url) == CrawlAction.STORE) {
			boolean saveFlag = true;
			if (topicCrawler) {// topic specifi crawler mode
				saveFlag = topicFilter(pageInfo);
			}
			if (saveFlag) {
				topicSpecific++;
				logger.info(String.format("Save page: %s", url));
				for (IPageSaver pageListener : pageListeners) {
					try {
						pageListener.savePage(pageInfo);
					} catch (Exception e) {
						logger.error("PageListener error", e);
					}
				}
			}
			// if map not contains url save it
			if (crawlHistory != null) {
				if (crawlHistory.getBufferSet().size() >= crawlHistory
						.getBufferSize()) {
					synchronized (crawlHistory.getBufferSet()) {
						crawlHistory.addHistory();
					}
				} else {
					crawlHistory.addToBufferSet(url);
				}
			}

			// add url to BloomFilter
			bloomFilter.add(url);

			if (topicCrawler) {// topic specify crawler mode
				if (topicCrawlHistory != null) {
					if (topicCrawlHistory.getBufferSet().size() >= topicCrawlHistory
							.getBufferSize()) {
						synchronized (topicCrawlHistory.getBufferSet()) {
							topicCrawlHistory.addHistory();
						}
					} else {
						topicCrawlHistory.addToBufferSet(url);
					}
				}
			}
		}
	}

	private boolean topicFilter(PageInfo pageInfo) {
		String content = pageInfo.getContent();
		boolean flag = true;
		// BlockExtractor be = new BlockExtractor();
		// be.setReader(new StringReader(content));
		// be.extract();
		// String title = be.getTitle();
		// content = be.getContent();
		SimpleHtmlExtractor she = new SimpleHtmlExtractor();
		she.setReader(new StringReader(content));
		she.extract();
		// content = she.getContent();
		String title = she.getTitle();
		String url = pageInfo.getUrl();
		for (String topicWord : topicWords) {
			if (!title.contains(topicWord)) {
				flag = false;
				break;
			}
		}
		if (flag)
			pairs.put(url, title);
		return flag;
	}

	@Override
	public void reportLinks(List<String> urls, int newDistance) {
		if (newDistance >= getMaxDepth()) {
			logger.debug("Distance %d too far away.  Do not add.", newDistance);
			return;
		}

		synchronized (dispatched) {
			for (String link : urls) {
				if (dispatched.contains(link)) {// 已抓过??
					logger.debug(String.format("Discard dispatched link %s",
							link));
					continue;
				}
				// if(crawlHistory!=null){
				// if(crawlHistory.getHistorySet().contains(link)) {
				// logger.info("some day already crawled this link:"+link);
				// continue;
				// }
				// }

				if (bloomFilter != null) {
					if (bloomFilter.contains(link)) {
						logger.info("Someday already crawled this link:" + link);
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
