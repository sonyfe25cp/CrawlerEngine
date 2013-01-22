package bit.crawl.extractor;

public interface ExtractorSink {
	void save(Object document, String dbName);
}
