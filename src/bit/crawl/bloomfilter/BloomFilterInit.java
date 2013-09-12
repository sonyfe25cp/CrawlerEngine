package bit.crawl.bloomfilter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import bit.crawl.crawler.CrawlHistory;

public class BloomFilterInit {

	private int elementCount; // Number of elements to test
	
	private double falsePositive;   //false positive probability
    
	/**
	 * Crawl history in database.
	 */
	private CrawlHistory crawlHistory = null;

	public int getElementCount() {
		return elementCount;
	}

	public void setElementCount(int elementCount) {
		this.elementCount = elementCount;
	}

	public double getFalsePositive() {
		return falsePositive;
	}

	public void setFalsePositive(double falsePositive) {
		this.falsePositive = falsePositive;
	}

	public CrawlHistory getCrawlHistory() {
		return crawlHistory;
	}

	public void setCrawlHistory(CrawlHistory crawlHistory) {
		this.crawlHistory = crawlHistory;
	}

	public void printStat(long start, long end) {
        double diff = (end - start) / 1000.0;
        System.out.println(diff + "s, " + (elementCount / diff) + " elements/s");
    }
	
    public void init() {        

        BloomFilter<String> bf = new BloomFilter<String>(falsePositive, elementCount);
        ResultSet rs;
        System.out.println("BitSet size is :" + bf.getBitSet().size());
        
        int j = 0;
        rs = crawlHistory.initBloomFilter();
        long start_add = System.currentTimeMillis();
        try {
			while(rs.next()){
				bf.add(rs.getString(1));
				j++;
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
      //序列化bloomfilter
      	FileOutputStream fos = null;
      	try {
      		fos = new FileOutputStream("bloomFilter.bf");
      	} catch (FileNotFoundException e) {
      		e.printStackTrace();
      	}
      	ObjectOutputStream oos = null;
      	try {
     		oos = new ObjectOutputStream(fos);
      		oos.writeObject(bf);
      		oos.close();
      		System.out.println("bloomFilter serialize successfully!!!");
      	} catch (IOException e) {
      		e.printStackTrace();
      	}
        
        j++;
        long end_add = System.currentTimeMillis();
        printStat(start_add, end_add);
        System.out.println("total number of elements added is : " + j);

        System.out.println("k is " + bf.getK());

    }
}
