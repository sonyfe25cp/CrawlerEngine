package bit.crawl.crawler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * get the time format STORE and FOLLOW patterns needed
 * @author sliver
 *
 */
public class TimeFormat {
	
	/**
	 * standard time format, default value is yyyyMMdd
	 */
	private String standard;
	
	/**
	 * aim Store time format, the format needed
	 */
	private String aimStoreFormat;
	
	/**
	 * aim Follow time format
	 */
	private String aimFollowFormat;
	
	/**
	 * start page num
	 */
	private int begin;
	
	/**
	 * end page num
	 */
	private int end;

	/**
	 * store time format after convert
	 */
	private String storeTimeFormat;
	
	/**
	 * follow time format after convert
	 */
	private String followTimeFormat;
	
	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public String getAimStoreFormat() {
		return aimStoreFormat;
	}

	public void setAimStoreFormat(String aimStoreFormat) {
		this.aimStoreFormat = aimStoreFormat;
	}

	public String getAimFollowFormat() {
		return aimFollowFormat;
	}

	public void setAimFollowFormat(String aimFollowFormat) {
		this.aimFollowFormat = aimFollowFormat;
	}

	public String getFollowTimeFormat() {
		return followTimeFormat;
	}

	public void setFollowTimeFormat(String followTimeFormat) {
		this.followTimeFormat = followTimeFormat;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	public String getStoreTimeFormat() {
		return storeTimeFormat;
	}

	public void setStoreTimeFormat(String storeTimeFormat) {
		this.storeTimeFormat = storeTimeFormat;
	}

	/**
	 * A bean-style empty constructor
	 * 
	 * @author sliver
	 */
	public TimeFormat(){
		
	}
	public String toStoreFormat(String date){
		String result = null;
		SimpleDateFormat time = new SimpleDateFormat(aimStoreFormat);
		SimpleDateFormat legaltime = new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer sb = new StringBuffer(date);
		sb.insert(4, '-');
		sb.insert(7, '-');
		String legal = sb.toString();
		Date d = new Date();
System.out.println("legal format is : " + legal);
		try {
			d = legaltime.parse(legal);
		} catch (ParseException e) {
			e.printStackTrace();
		}
System.out.println(time.format(d));
		result = time.format(d);
		return result;
	}
	
	public String toFollowFormat(String date){
		String result = null;
		SimpleDateFormat time = new SimpleDateFormat(aimFollowFormat);
		SimpleDateFormat legaltime = new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer sb = new StringBuffer(date);
		sb.insert(4, '-');
		sb.insert(7, '-');
		String legal = sb.toString();
		Date d = new Date();
System.out.println("legal format is : " + legal);
		try {
			d = legaltime.parse(legal);
		} catch (ParseException e) {
			e.printStackTrace();
		}
System.out.println(time.format(d));
		result = time.format(d);
		return result;
	}
	

}
