package bit.crawl.extractor.classic.xpath;

import java.util.HashMap;

public interface Sink {
	public void sink(HashMap<String, Object> input, String output);
	public void sink(HashMap<String, Object> input);
	public void setOutput(String output);
}
