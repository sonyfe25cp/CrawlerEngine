package bit.crawl.cmdline;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bit.crawl.bloomfilter.BloomFilterInit;

public class CmdlineBloomFilterRunner {

	static String USAGE = "USAGE: java bit.crawl.cmdline.CmdlineCreateBloomFilterRunner taskfile.xml";
	
	public static void main(String[] args) {
		
		if(args.length != 1){
			System.out.println(USAGE);
		}
		
		for(String taskFileName : args){
			ApplicationContext context = new FileSystemXmlApplicationContext("file:" + taskFileName);
			BloomFilterInit bloomFilterInit = context.getBean("bloomFilterInit", BloomFilterInit.class);
			bloomFilterInit.init();	
		}
		
		

	}

}
