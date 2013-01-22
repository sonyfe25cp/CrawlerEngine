package bit.crawl.cmdline;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bit.crawl.crawler.Crawler;
import bit.crawl.util.Logger;

public class CrawlRunnerMain {
	static Logger logger = new Logger();

	public static void main(String[] args1) {
		String taskFileName = "/home/coder/workspace/mavenSpace/CrawlerEngine/real-world-tasks/bitunion-forum-14-1.xml";

		ApplicationContext context = new FileSystemXmlApplicationContext(
				"file:" + taskFileName);
		Crawler crawler = context.getBean("crawler", Crawler.class);
		crawler.run();
	}

}