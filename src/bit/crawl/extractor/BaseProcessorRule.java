package bit.crawl.extractor;

public abstract class BaseProcessorRule implements ProcessorRule {

	private ExtractorProcessor processor;
	private String dbName;

	public BaseProcessorRule() {
		super();
	}

	@Override
	public ExtractorProcessor getProcessor() {
		return this.processor;
	}

	public void setProcessor(ExtractorProcessor processor) {
		this.processor = processor;
	}

	@Override
	public String getDbName() {
		return this.dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

}