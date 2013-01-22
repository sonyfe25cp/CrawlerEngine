package bit.crawl.cmdline;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bit.crawl.extractor.Extractor;
import bit.crawl.extractor.ExtractorSource;
import bit.crawl.extractor.FileBasedExtractorSource;
import bit.crawl.util.Logger;

public class CmdlineExtractRunner {
	static String USAGE = "USAGE: java bit.crawl.cmdline.CmdlineExtractorRunner taskfile.spring.xml [stored-pages.pages]";

	static Logger logger = new Logger();

	public static void main(String[] args) {
		if (args.length < 1 || args.length > 2) {
			System.out.println(USAGE);
		}

		String taskFileName = args[0];

		ApplicationContext context = new FileSystemXmlApplicationContext(
				"file:" + taskFileName);

		Extractor extractor = context.getBean("extractor", Extractor.class);

		ExtractorSource source = extractor.getSource();
		if (source instanceof FileBasedExtractorSource && args.length >= 2) {
			FileBasedExtractorSource fbes = (FileBasedExtractorSource) source;
			String path = args[1];
			fbes.setPath(path);
		}

		logger.info("Begin extracting.");
		extractor.processAll();
		logger.info("Extraction complete.");
	}
}
