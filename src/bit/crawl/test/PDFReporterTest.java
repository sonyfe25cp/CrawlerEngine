package bit.crawl.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bit.crawl.reporter.PDFReporter;

import com.itextpdf.text.DocumentException;

public class PDFReporterTest {

	public static void main(String[] args) throws FileNotFoundException {
		
		String title = "BIT网络监控系统";
		String site = "123网";
		
		List<String> words = new ArrayList<String>();
		words.add("北京");
		words.add("和谐");
		words.add("战争");
		
		int total = 1002;
		int topicSpecific = 20;
		
		HashMap<String,String> pairs = new HashMap<String,String>();
		pairs.put("http://www.123.com", "我爱北京天安门");
		pairs.put("http://www.1234.com", "我爱北京天安门e");
		pairs.put("http://www.1234.com", "我爱北京天安门d");
		pairs.put("http://www.1231.com", "我爱北京天安门a");
		pairs.put("http://www.12323.com", "我爱北京天安门d");
		pairs.put("http://www.1234124.com", "我爱北京天安f门");
		pairs.put("http://www.123ad.com", "我爱北京天安门sa");
		pairs.put("http://www.123sd.com", "我爱北京天安门asdf");
		pairs.put("http://www.123e.com", "我爱北京天安门cc");
		pairs.put("http://www.123dd.com", "我爱北京天安门qwe");
		
		
		PDFReporter pdf = new PDFReporter();
		
		pdf.setTitle(title);
		pdf.setSite(site);
		pdf.report(words,total,topicSpecific,pairs);
		
		
	}

}
