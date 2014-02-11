package bit.crawl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import bit.crawl.bloomfilter.BloomFilter;
public class BloomFilterUtils {
    private static Logger logger = new Logger();

	//序列化bloomfilter
	public static void outputBloomFilter(BloomFilter bf, String bloomPath){
      	FileOutputStream fos = null;
      	try {
      		fos = new FileOutputStream(bloomPath);
      	} catch (FileNotFoundException e) {
      		e.printStackTrace();
      	}
      	ObjectOutputStream oos = null;
      	try {
     		oos = new ObjectOutputStream(fos);
      		oos.writeObject(bf);
      		oos.close();
      		fos.close();
      		logger.info("bloomFilter 成功序列化到磁盘!!!");
      	} catch (IOException e) {
      		e.printStackTrace();
      	}
	}

	// 加载BloomFilter
	public static BloomFilter<String> bootBloomFilter(String bloomPath){
		BloomFilter<String> bloomFilter = null;
		File tmpFile = new File(bloomPath);
		if(!tmpFile.exists()){
			try {
				tmpFile.createNewFile();
				bloomFilter = new BloomFilter<String>(0.0000001, 10000 * 10000);
				return bloomFilter;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(bloomPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(fis);
			bloomFilter = (BloomFilter<String>) ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
		    logger.error("bloomfilter不匹配，程序将自动退出.");
		    System.exit(0);
		}
		return bloomFilter;
	}
}
