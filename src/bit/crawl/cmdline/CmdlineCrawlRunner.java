package bit.crawl.cmdline;

import java.io.File;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bit.crawl.crawler.Crawler;
import bit.crawl.task.CrawlTask;
import bit.crawl.task.CrawlTaskSpec;
import bit.crawl.task.XMLCrawlTaskSpecFactory;

public class CmdlineCrawlRunner {
	static String USAGE = "USAGE: java bit.crawl.cmdline.CmdlineTaskRunner taskfile1.xml [taskfile2.xml ...]";

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println(USAGE);
		}

		XMLCrawlTaskSpecFactory factory = new XMLCrawlTaskSpecFactory();

		for (String taskFileName : args) {
			if (taskFileName.endsWith(".spring.xml")) {
				ApplicationContext context = new FileSystemXmlApplicationContext(
						"file:" + taskFileName);
				Crawler crawler = context.getBean("crawler", Crawler.class);
				crawler.run();
			} else {
				CrawlTaskSpec taskSpec = factory.load(new File(taskFileName));
				CrawlTask task = taskSpec.createCrawlTask();
				task.run();
			}
		}
	}
}
