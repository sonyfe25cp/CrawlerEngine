package bit.crawl.crawler.impl;

import java.nio.charset.Charset;
import java.util.List;

import bit.crawl.crawler.PageInfo;

public interface ICrawlerForWorker {
	Charset getCharset();
	void reportPageFetched(PageInfo pageInfo);
	void reportLinks(List<String> urls, int newDistance, String url);
	void reportJobDone();
}
