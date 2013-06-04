package bit.crawl.reporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFReporter {

	
	private String title;
	private String site;
	private List<String> words;
	private int total;
	private int topicSpecific;
	private HashMap<String,String> pairs;
	
	private String fileName;
	private static String fontPath="msyh.ttf";
	
	public PDFReporter() {
		super();
	}

	BaseFont fontChinese;
	Font titleFont;
	Font contentFont;
	private void init(){
		
		this.fileName = this.site +"-"+today("yyyyMMdd")+".pdf";
		File file = new File(fileName);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			fontChinese=BaseFont.createFont(fontPath,BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		titleFont = new Font(fontChinese, 17, Font.BOLD);
		contentFont = new Font(fontChinese, 12);
	}
	
	private String today(String format){
		Date now=new Date();
		SimpleDateFormat f=new SimpleDateFormat(format);
		return f.format(now);
	}
	
	public void report(List<String> words,int total, int topicSpecific, HashMap<String,String> pairs) {
		this.words = words;
		this.total = total;
		this.topicSpecific = topicSpecific;
		this.pairs = pairs;
		try {
			toPdf();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void toPdf() throws DocumentException, IOException{
		init();
		Document document = new Document(PageSize.A4, 50, 50, 50, 50);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
		document.open();
		
    	Paragraph title1 = new Paragraph(title, titleFont);
		
    	Chapter chapter1 = new Chapter(title1, 1);
		chapter1.setNumberDepth(0);
		document.add(chapter1);
		
		document.add(new Paragraph("监控网站："+site,	contentFont));
		document.add(new Paragraph("监控时间："+today("yyyy-MM-dd"),contentFont));
    	document.add(new Paragraph("监控词："+topicWords(),contentFont));
    	document.add(new Paragraph("本次监控页面总数："+total+" 个",contentFont));
    	document.add(new Paragraph("需要监控页面数："+topicSpecific+" 个",contentFont));
    	document.add(new Paragraph("需要监控页面标题及URL如下：",contentFont));
    	int i = 1;
    	for(Entry<String,String> entry : pairs.entrySet()){
    		document.add(new Paragraph("      "+i+"）"+entry.getValue()+"    "+entry.getKey(),contentFont));
    		i++;
    	}
    	document.close();
	}

	private String topicWords(){
		String line = "";
		for(String word:words){
			line += word+" ";
		}
		return line;
	}

	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getSite() {
		return site;
	}


	public void setSite(String site) {
		this.site = site;
	}


	public List<String> getWords() {
		return words;
	}


	public void setWords(List<String> words) {
		this.words = words;
	}


	public int getTotal() {
		return total;
	}


	public void setTotal(int total) {
		this.total = total;
	}


	public int getTopicSpecific() {
		return topicSpecific;
	}


	public void setTopicSpecific(int topicSpecific) {
		this.topicSpecific = topicSpecific;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public HashMap<String, String> getPairs() {
		return pairs;
	}


	public void setPairs(HashMap<String, String> pairs) {
		this.pairs = pairs;
	}
	
}
