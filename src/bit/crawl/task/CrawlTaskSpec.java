package bit.crawl.task;

import java.util.*;
import java.util.regex.Pattern;
import bit.crawl.crawler.*;
import bit.crawl.extractor.*;

public class CrawlTaskSpec {
	private String taskName;
	private String baseDir;
	private int maxThreads;
	private int maxDepth;
	private String encoding;
	private List<String> initialUrls = new ArrayList<String>();
	private List<SiteConfig> siteConfigs = new ArrayList<SiteConfig>();
	private List<FilterRule> filterRules = new ArrayList<FilterRule>();

	private List<Extractor> pageExtractors = new ArrayList<Extractor>();

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

	public int getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
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

	public List<Extractor> getPageExtractors() {
		return pageExtractors;
	}

	public void setPageExtractors(List<Extractor> pageExtractors) {
		this.pageExtractors = pageExtractors;
	}

	public void addInitialUrl(String url) {
		this.initialUrls.add(url);
	}

	public void addSiteConfig(SiteConfig siteConfig) {
		this.siteConfigs.add(siteConfig);
	}

	public void addSiteConfig(String host, int fetchDelay) {
		this.siteConfigs.add(new SiteConfig(host, fetchDelay));
	}

	public void addFilterRule(FilterRule filterRule) {
		this.filterRules.add(filterRule);
	}

	public void addFilterRule(String pattern, Boolean negative, String action) {
		if (negative == null) {
			negative = false;
		}
		this.filterRules.add(new FilterRule(Pattern.compile(pattern), negative,
				CrawlAction.valueOf(action)));
	}
	
	public void addPageExtractor(Extractor pageExtractor) {
		this.pageExtractors.add(pageExtractor);
	}
	
	
	public CrawlTaskSpec() {
		super();
	}
	
	
	public CrawlTask createCrawlTask() {
		CrawlTask crawlTask = new CrawlTask();
		
		crawlTask.setTaskName(taskName);
		crawlTask.setBaseDir(baseDir);

		crawlTask.setMaxThreads(maxThreads);
		crawlTask.setMaxDepth(maxDepth);
		crawlTask.setEncoding(encoding);
		crawlTask.getInitialUrls().addAll(initialUrls);
		crawlTask.getSiteConfigs().addAll(siteConfigs);
		crawlTask.getFilterRules().addAll(filterRules);
		
		
		return crawlTask;
	}
}
