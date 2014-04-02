package bit.crawl.cmdline;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bit.crawl.crawler.Crawler;
import bit.crawl.util.Logger;

public class CrawlRunnerMain {
	static Logger logger = new Logger();

	public static void main(String[] args1) {

		// 腾讯新闻
		String taskFileName1 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-qq.spring.xml";
		// 人民网
		String taskFileName2 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-renmin.spring.xml";
		// 中国网
		String taskFileName3 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-china.spring.xml";
		// 中国日报
		String taskFileName4 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-chinadaily.spring.xml";
		// 中国新闻网
		String taskFileName5 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-chinanews.spring.xml";
		// 中国青年网
		String taskFileName6 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-youth.spring.xml";
		// 齐鲁网
		String taskFileName7 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-qilu.spring.xml";
		// 北方网
		String taskFileName8 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-north.spring.xml";
		// 东方网
		String taskFileName9 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-eastday.spring.xml";
		// 凤凰网
		String taskFileName10 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-fenghuang.spring.xml";
		// 参考消息
		String taskFileName11 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-cankaoxiaoxi.spring.xml";
		// 联合早报
		String taskFileName12 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-zaobao.spring.xml";
		// 新浪网
		String taskFileName13 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-sina.spring.xml";
		// 搜狐网
		String taskFileName14 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-sohu.spring.xml";
		// 东南网
		String taskFileName15 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-dongnan.spring.xml";

		// 智联招聘
		String taskFileName16 = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/newsgn-zhaopin.spring.xml";
		String taskFileName17 = "/Users/omar/workspace/CrawlerEngine/real-world-tasks/zhaopin-beijing.spring.xml";
		String taskFileName18 = "/Users/omar/workspace/CrawlerEngine/real-world-tasks/zhaopin-beijing-test.spring.xml";
		String taskFileName19 = "/Users/omar/workspace/CrawlerEngine/real-world-tasks/zhaopin-ali.spring.xml";
		
		String taskFileName20 = "/home/coder/git/CrawlerEngine/real-world-tasks/newsgn-qq-new.spring.xml";
		
		ApplicationContext context = new FileSystemXmlApplicationContext(
				"file:" + taskFileName20);
		Crawler crawler = context.getBean("crawler", Crawler.class);
		crawler.run();
		System.out.println("a");
	}
}
