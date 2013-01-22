package bit.crawl.extractor.xpath;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

public class Demo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * 创建NewsExtractor的实例，并使用src/bit/crawl/extractor/xpath/sina.xml文件进行配置
		 */
		PageExtractor extractor = new NewsExtractor()
				.configWith(new XmlConfigurationImpl(
						"src/bit/crawl/extractor/xpath/sina.xml")).trimResult();
		
		/*
		 * 对文件进行抽取
		 * 输入(File类，字符集)  或者输入(String fileContent)
		 * 输出<标签-内容>对  例如：publictime  2011年09月14日 09:20
		 */
		HashMap<String, String> result = extractor.extract(new File("/home/lins/data/yahoo/page/yahoo_1"),
				"UTF-8");
		
		System.out.println(result);
		
		/*
		 * 使用ForumExtractor进行实例化
		 * *输入(File类，字符集)  或者输入(String fileContent)
		 * 输出<标签+id-内容>对  例如：username2 Julius
		 * username2就是 标签+id
		 */
		extractor = new ForumExtractor().configWith(new XmlConfigurationImpl(
				"src/bit/crawl/extractor/xpath/bitpt.xml")).trimResult();
		result = extractor.extract(new File("/home/lins/document/test1"),
				"UTF-8");
		
		System.out.println(result);
	}

}
