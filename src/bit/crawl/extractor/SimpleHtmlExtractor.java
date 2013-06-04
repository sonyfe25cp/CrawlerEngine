package bit.crawl.extractor;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class SimpleHtmlExtractor {
	
	private Logger log = Logger.getLogger(SimpleHtmlExtractor.class);
	
	private IPreTreatment preTreatHandler=new FixHtmlErrorHandler();
	
	private String title;
	private String content;
	
	private Reader reader;
	private String html;
	
	public void extract(){
		List<String> lines=new ArrayList<String>();
		
		
		if(reader==null){
			log.error("no reader available~~");
			return;
		}
		if(preTreatHandler!=null){
			if(html==null){//预制html内容
				html=preTreatHandler.readFromReader(reader);//文件读取
			}
			this.title=preTreatHandler.extractTitle(html);
			lines=preTreatHandler.preTreat(html);
		}else{
			System.out.println("error");
		}
		StringBuilder sb=new StringBuilder();
		for(String line:lines){
			int totaLength=line.length();
			String content=line.replaceAll("<(.|\n)*?>", "").trim();
			int contentLength=content.length();
			float den=(contentLength+0.0f)/totaLength;
			if(den>0.5){
				log.debug(content+"  den: "+den);
				sb.append(content+"\n");
			}
		}
		this.content=sb.toString();
	}
	public IPreTreatment getPreTreatHandler() {
		return preTreatHandler;
	}
	public void setPreTreatHandler(IPreTreatment preTreatHandler) {
		this.preTreatHandler = preTreatHandler;
	}
	public String getTitle() {
		return title;
	}
	public String getContent() {
		return content;
	}
	public Reader getReader() {
		return reader;
	}
	public void setReader(Reader reader) {
		this.reader = reader;
	}
	public void setHtml(String html) {
		this.html = html;
	}

}
