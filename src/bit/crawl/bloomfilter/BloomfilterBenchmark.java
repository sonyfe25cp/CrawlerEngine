/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bit.crawl.bloomfilter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bit.crawl.crawler.CrawlHistory;

/**
 * A (very) simple benchmark to evaluate the performance of the Bloom filter class.
 *
 * @author Magnus Skjegstad
 */
public class BloomfilterBenchmark {
    static int elementCount = 1000000; // Number of elements to test
    
	/**
	 * Crawl history in database.
	 */
	private CrawlHistory initBloomFilter = null;

    public CrawlHistory getInitBloomFilter() {
		return initBloomFilter;
	}

	public void setInitBloomFilter(CrawlHistory initBloomFilter) {
		this.initBloomFilter = initBloomFilter;
	}

	public static void printStat(long start, long end) {
        double diff = (end - start) / 1000.0;
        System.out.println(diff + "s, " + (elementCount / diff) + " elements/s");
    }
	

    public static void main(String[] argv) {
        

        final Random r = new Random();

        // Generate elements first
        List<String> existingElements = new ArrayList(elementCount);
        for (int i = 0; i < elementCount; i++) {
            byte[] b = new byte[200];
            r.nextBytes(b);
            existingElements.add(new String(b));
        }
        
        List<String> nonExistingElements = new ArrayList(elementCount);
        for (int i = 0; i < elementCount; i++) {
            byte[] b = new byte[200];
            r.nextBytes(b);
            nonExistingElements.add(new String(b));
        }

        BloomFilter<String> bf = new BloomFilter<String>(0.0001, elementCount);
        ResultSet rs;
        System.out.println("BitSet size is :" + bf.getBitSet().size());
        
        int j = 0;
        //rs = initBloomFilter.initBloomFilter();
        rs = urlDAO.getUrl();
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
       // bf.add("http://news.qq.com/a/20130904/013446.htm");
        
      //–Ú¡–ªØ
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
      			System.out.println("bloomFilter serialize successfully£°");
      		} catch (IOException e) {
      			e.printStackTrace();
      		}
        
        j++;
        long end_add = System.currentTimeMillis();
        printStat(start_add, end_add);
        System.out.println("total number of elements added is : " + j);

        System.out.println("Testing " + elementCount + " elements");
        System.out.println("k is " + bf.getK());

        // Add elements
//        System.out.print("add(): ");
//        long start_add = System.currentTimeMillis();
//        for (int i = 0; i < elementCount; i++) {
//            bf.add(existingElements.get(i));
//        }
//        long end_add = System.currentTimeMillis();
//        printStat(start_add, end_add);

        // Check for existing elements with contains()
        System.out.print("contains(), existing: ");
        long start_contains = System.currentTimeMillis();
        for (int i = 0; i < elementCount; i++) {
            bf.contains(existingElements.get(i));
        }
        long end_contains = System.currentTimeMillis();
        printStat(start_contains, end_contains);

        // Check for existing elements with containsAll()
        System.out.print("containsAll(), existing: ");
        long start_containsAll = System.currentTimeMillis();
        for (int i = 0; i < elementCount; i++) {
            bf.contains(existingElements.get(i));
        }
        long end_containsAll = System.currentTimeMillis();
        printStat(start_containsAll, end_containsAll);

        // Check for nonexisting elements with contains()
        System.out.print("contains(), nonexisting: ");
        long start_ncontains = System.currentTimeMillis();
        for (int i = 0; i < elementCount; i++) {
            bf.contains(nonExistingElements.get(i));
        }
        long end_ncontains = System.currentTimeMillis();
        printStat(start_ncontains, end_ncontains);

        // Check for nonexisting elements with containsAll()
        System.out.print("containsAll(), nonexisting: ");
        long start_ncontainsAll = System.currentTimeMillis();
        for (int i = 0; i < elementCount; i++) {
            bf.contains(nonExistingElements.get(i));
        }
        long end_ncontainsAll = System.currentTimeMillis();
        printStat(start_ncontainsAll, end_ncontainsAll);

    }
    
    
}
