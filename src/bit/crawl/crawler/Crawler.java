package bit.crawl.crawler;

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
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import bit.crawl.bloomfilter.BloomFilter;
import bit.crawl.crawler.impl.FetchJob;
import bit.crawl.crawler.impl.ICrawlerForWorker;
import bit.crawl.extractor.SimpleHtmlExtractor;
import bit.crawl.reporter.PDFReporter;
import bit.crawl.util.BloomFilterUtils;
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
	
	
	private static String BASICREFERURL = "http://www.baidu.com";

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
	
	private String bloomPath = "/Users/omar/software/crawlerengine/jobs/zhaopin-crawler.bf";//单个bloomFilter文件地址
//	private String bloomPath;//单个bloomFilter文件地址

	private boolean bloomFlag = true;
	
	/**
	 * time format configure
	 */
	private TimeFormat timeFormat = null;

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
	
	
	public TimeFormat getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(TimeFormat timeFormat) {
		this.timeFormat = timeFormat;
	}

	/**
	 * A bean-style empty constructor.
	 * 
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
	 * @param listeer
	 *            The PageListener to add;
	 */
	public void addPageListener(IPageSaver listener) {
		pageListeners.add(listener);
	}

	// 加载BloomFilter
	private void bootBloomFilter(){
		if(bloomPath == null || bloomPath.length() == 0){
			this.bloomFlag = false;
			return ;
		}
		logger.info("加载blommfilter: " + bloomPath);
		bloomFilter = BloomFilterUtils.bootBloomFilter(bloomPath);
	}
	
	public List<String> resetInitialUrls(){
	    List<String> newInitialUrls = new ArrayList<String>();
	    int begin = timeFormat.getBegin();
        int end = timeFormat.getEnd();
        
        List<FilterRule> frs = new ArrayList<FilterRule>();
        List<FilterRule> original = getFilterRules();
        
        if ((begin != 0) && (end != 0) && (begin < end)) {//通过begin/end配置抓取多日新闻
            for(int i = timeFormat.getBegin(); i <= timeFormat.getEnd(); i++){
                for (String link : getInitialUrls()) {
                    if (link.matches("(.*)#(.*)#(.*)")) {
                        String[] substring = link.split("#");
                        String date = Integer.toString(i);
                        StringBuilder sb = new StringBuilder();
                        sb.append(substring[0]);
                        sb.append(date);
                        sb.append(substring[2]);
                        link = sb.toString();
                        newInitialUrls.add(link);
                        String storeFormat = timeFormat.toStoreFormat(date);
                        timeFormat.setStoreTimeFormat(storeFormat);
                        String followFormat = timeFormat.toFollowFormat(date);
                        timeFormat.setFollowTimeFormat(followFormat);
                        for (FilterRule fr : original) {
                            FilterRule fre = new FilterRule();
                            fre.setAction(fr.getAction());
                            fre.setNegative(fr.isNegative());
                            fre.setPattern(fr.getPattern());
                            Pattern p = fr.getPattern();
                            
                            String pstring = null;
                            pstring = p.toString();
//                          System.out.println("********regex is " + pstring);
                            if (pstring.contains("#aimStoreFormat#")){
                                pstring = pstring.replaceFirst("#aimStoreFormat#", timeFormat.getStoreTimeFormat());
                                System.out.println("#######replaced pattern content is " + pstring);
                                fre.setPattern(Pattern.compile(pstring));
                                frs.add(fre);
                            }
                            else if (pstring.contains("#aimFollowFormat#")){
                                pstring = pstring.replaceFirst("#aimFollowFormat#", timeFormat.getFollowTimeFormat());
//                              System.out.println("########replaced pattern content is " + pstring);
                                fre.setPattern(Pattern.compile(pstring));
                                frs.add(fre);
                            }
                            else if (pstring.equals(".*")){
                                if (i == timeFormat.getEnd()) {
                                    frs.add(fre);
                                }
                            }
                                                        
                        }
                    }
                    else
                        newInitialUrls.add(link);
                }
            }
            
            setFilterRules(frs);
        }else{  //新加///////////////////convert InitialUrls into standard format
            for(String link : getInitialUrls()){
                if(link.matches("(.*)#\\d{8}#(.*)")){                //如果种子链接里包含#20140111#式样，进行url转换
                    String[] substring = link.split("#");
                    String date = substring[1];
                    StringBuilder sb = new StringBuilder();
                    for(String str : substring)
                        sb.append(str);
                    link = sb.toString();
                    newInitialUrls.add(link);
                    String storeFormat = timeFormat.toStoreFormat(date);
                    timeFormat.setStoreTimeFormat(storeFormat);
                    String followFormat = timeFormat.toFollowFormat(date);
                    timeFormat.setFollowTimeFormat(followFormat);
                }
                else{//规范形式，无需转换
                    newInitialUrls.add(link);
                }
            }
            //convert patterns
            for (FilterRule fr : getFilterRules()) {
                for (Pattern p : fr.getPatterns()) {
                    String pstring = null;
                    pstring = p.toString();
                    if (pstring.contains("#aimStoreFormat#")){
                        pstring = pstring.replaceFirst("#aimStoreFormat#", timeFormat.getStoreTimeFormat());
                        fr.setPattern(Pattern.compile(pstring));
                    }
                    else if (pstring.contains("#aimFollowFormat#")){
                        pstring = pstring.replaceFirst("#aimFollowFormat#", timeFormat.getFollowTimeFormat());
                        fr.setPattern(Pattern.compile(pstring));
                    }
                }
                frs.add(fr);
            }
            //new FileRules after convert
            setFilterRules(frs);
        }
        return newInitialUrls;
	}
	
	/**
	 * Start crawling, save pages in pageInfos. It will return when all
	 * reachable pages are crawled.
	 */
	public void run() {
		try {
			
			logger.info("Start crawling.");
			logger.info("Default charset:" + getEncoding());

			//加载bf
			bootBloomFilter();
			if(!bloomFlag){
				if(crawlHistory!=null){
				    logger.info("Loading crawl history");
					crawlHistory.loadHistory();
				}
				if (topicCrawler && topicCrawlHistory != null) {
					topicCrawlHistory.loadHistory();
				}
			}
			executor = new ThreadPoolExecutor(getMaxThreads(), getMaxThreads(),
					0, TimeUnit.SECONDS, workQueue);
			
			List<String> newInitialUrls = resetInitialUrls();
			
			reportLinks(newInitialUrls, 0, BASICREFERURL);
			
			latch.await();
			
			if (crawlHistory != null) {
				if (!crawlHistory.getBufferSet().isEmpty()) {
					crawlHistory.addHistory();
				}
			}
			if (topicCrawler && topicCrawlHistory != null) {
				if (!topicCrawlHistory.getBufferSet().isEmpty()) {
					topicCrawlHistory.addHistory();
				}
			}
		} catch (InterruptedException e) {
			logger.info("Crawling interrupted.");
		} finally {
			if (executor != null) {
				executor.shutdownNow();
			}
		}
		if(bloomFlag){
			//序列化输出bloomFilter
		    BloomFilterUtils.outputBloomFilter(bloomFilter, bloomPath);
		}
		logger.info("Crawling stopped.");
		if(topicCrawler){
			logger.info("Begin to generate the report.");
			pdfReporter.report(topicWords, total, topicSpecific, pairs);
			logger.info("Generete the report successfully.");
		}
	}
	private CrawlAction getFilterAction(String url) {
		CrawlAction action = null;
		for (FilterRule fr : getFilterRules()){
			action = fr.judge(url);
			if (action != null) {
				return action;
			}
		}
		logger.info("url:" + url + " --- action:" + (action == null ? "Follow" : action));
		return CrawlAction.FOLLOW;
	}

	@Override
	public void reportPageFetched(PageInfo pageInfo) {
		String url = pageInfo.getUrl();
		total++;
		if (getFilterAction(url) == CrawlAction.STORE || getFilterAction(url) == CrawlAction.FOLLOW_STORE) {
			boolean saveFlag = true;
			if (topicCrawler) {// topic specify crawler mode
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
			if(bloomFlag){
				bloomFilter.add(url);
			}
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
	public void reportLinks(List<String> urls, int newDistance, String url) {
		if (newDistance >= getMaxDepth()) {
			logger.debug("Distance %d too far away.  Do not add.", newDistance);
			return;
		}

		synchronized (dispatched) {
			for (String link : urls) {
				if (dispatched.contains(link)) {// 已抓过??
					logger.debug(String.format("Discard dispatched link %s", link));
					continue;
				}
				 if(crawlHistory!=null){
					 if(crawlHistory.getHistorySet().contains(link)) {
					 logger.info("some day already crawled this link:"+link);
					 continue;
					 }
				 }
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
				pageInfo.setReferURL(url);
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
