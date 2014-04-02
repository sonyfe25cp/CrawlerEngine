package bit.crawl.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.digester.Digester;

import bit.crawl.crawler.SiteConfig;

/**
 * Loads CrawlTaskSpec objects from XML files.
 * 
 * @author Kunshan Wang
 * 
 */
public class XMLCrawlTaskSpecFactory {

	public CrawlTaskSpec load(String file) {
		return load(new File(file));
	}

	public CrawlTaskSpec load(File file) {
		try {
			return load(new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8")));
		} catch (Exception e) {
			throw new XMLConfigException(e);
		}
	}

	public CrawlTaskSpec load(Reader fileReader) {
		Digester d = new Digester();

		addRules(d);

		try {
			d.parse(fileReader);
		} catch (Exception e) {
			throw new XMLConfigException(e);
		}

		return (CrawlTaskSpec) d.getRoot();
	}

	private void addRules(Digester d) {
		d.addObjectCreate("crawl-task", CrawlTaskSpec.class);
		d.addCallMethod("crawl-task/task-name", "setTaskName", 0);
		d.addCallMethod("crawl-task/base-dir", "setBaseDir", 0);
		d.addCallMethod("crawl-task/max-threads", "setMaxThreads", 0, new Class<?>[]{int.class});
		d.addCallMethod("crawl-task/max-depth", "setMaxDepth", 0, new Class<?>[]{int.class});
		d.addCallMethod("crawl-task/encoding", "setEncoding", 0);
		d.addCallMethod("crawl-task/initial-urls/url", "addInitialUrl", 0);

		d.addObjectCreate("crawl-task/site-configs", SiteConfig.class);
		d.addSetNext("crawl-task/site-configs", "addSiteConfig");
		d.addCallMethod("crawl-task/site-configs/host", "setHost", 0);
		d.addCallMethod("crawl-task/site-configs/fetch-delay", "setFetchDelay",
				0);

		d.addCallMethod("crawl-task/filter-rules/rule", "addFilterRule", 3,
				new Class<?>[] { String.class, Boolean.class, String.class });
		d.addCallParam("crawl-task/filter-rules/rule", 0);
		d.addCallParam("crawl-task/filter-rules/rule", 1, "negative");
		d.addCallParam("crawl-task/filter-rules/rule", 2, "action");

	}
}
