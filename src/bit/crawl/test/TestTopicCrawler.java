package bit.crawl.test;

import java.io.File;
import java.util.ArrayList;

import bit.crawl.store.PageStoreReader;
import bit.crawl.store.StoredPage;

/**
 * @author Sonyfe25cp
 * 2013-5-12
 */
public class TestTopicCrawler {

	/**
	 * 测试主题爬虫效果
	 */
	public static void main(String[] args) {
		String path="/Users/omar/workspace/mavenworkspace/CrawlerEngine/build/dist/crawlerengine/crawled-pages/newsgn_qq_2013-05-12-22-42-18.pages";
		File testFile=new File(path);
		PageStoreReader psr=new PageStoreReader(testFile);
		ArrayList<StoredPage> list=psr.loadAll();
		System.out.println("该文件一共有:"+list.size()+"个文件");
		ArrayList topicWords = new ArrayList<String>();
		topicWords.add("北京");
		topicWords.add("和谐");
		topicWords.add("新疆");
		topicWords.add("文明");
		topicWords.add("事故");
		for(StoredPage page:list){
			
		}
		System.out.println("共:"+list.size()+"个文档");
	}

}
