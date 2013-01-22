package bit.crawl.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import bit.crawl.store.StoredPage;
import bit.crawl.util.Logger;

public class Extractor {
	static Logger logger = new Logger();

	private ExtractorSource source;
	private List<ProcessorRule> processorRules = new ArrayList<ProcessorRule>();
	private ExtractorSink sink;

	private int reportPerProcess = 500;

	public ExtractorSource getSource() {
		return source;
	}

	public void setSource(ExtractorSource source) {
		this.source = source;
	}

	public List<ProcessorRule> getProcessorRules() {
		return processorRules;
	}

	public void setProcessorRules(List<ProcessorRule> processorRules) {
		this.processorRules = processorRules;
	}

	public ExtractorSink getSink() {
		return sink;
	}

	public void setSink(ExtractorSink sink) {
		this.sink = sink;
	}

	public int getReportPerProcess() {
		return reportPerProcess;
	}

	public void setReportPerProcess(int reportPerProcess) {
		this.reportPerProcess = reportPerProcess;
	}

	public void processOne() throws NoSuchElementException {
		StoredPage sp = source.readPage();

		if (sp == null) {
			throw new NoSuchElementException("No more page to process.");
		}

		for (ProcessorRule pr : processorRules) {
			if (pr.isSuitableFor(sp)) {
				Object result;
				try {
					result = pr.getProcessor().extract(sp);
				} catch (Exception e) {
					logger.error("Error extracting document.", e);
					continue;
				}
				try {
					sink.save(result, pr.getDbName());
				} catch (Exception e) {
					logger.error("Error saving document.", e);
				}
				return;
			}
		}
	}

	public void processAll() {
		try {
			int processed = 0;
			while (true) {
				processOne();
				processed++;
				if (processed % reportPerProcess == 0) {
					logger.info("%d documents processed.", processed);
				}
			}
		} catch (NoSuchElementException e) {
			// Stop processing
		}
	}

}
