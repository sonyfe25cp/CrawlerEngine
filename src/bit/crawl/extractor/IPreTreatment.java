package bit.crawl.extractor;

import java.io.Reader;
import java.util.List;

public interface IPreTreatment {
	
	public String removeTags(String html);
	
	public List<String> preTreat(String total);
	
	public String extractTitle(String html);
	
	public String readFromReader(Reader reader);
	
	public String refineHtml(String html);

}
