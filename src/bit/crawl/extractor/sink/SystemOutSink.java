package bit.crawl.extractor.sink;

import bit.crawl.extractor.ExtractorSink;

public class SystemOutSink implements ExtractorSink {

	@Override
	public void save(Object document, String dbName) {
		System.out.println("==== SINKING INTO: "+dbName+" ====");
		System.out.println(document.toString());
		System.out.println("==== END DOCUMENT ====");
	}
}
