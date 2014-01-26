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

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


/**
 * A (very) simple benchmark to evaluate the performance of the Bloom filter class.
 *
 * @author Silver
 */
public class BloomfilterRunnerMain {

    public static void main(String[] argv) {
    	
    	String taskFileName = "/home/sliver/workspace2/CrawlerEngine/real-world-tasks/bloomFilter-config.spring.xml";

		ApplicationContext context = new FileSystemXmlApplicationContext("file:" + taskFileName);
		BloomFilterInit bloomFilterInit = context.getBean("bloomFilterInit", BloomFilterInit.class);

    	bloomFilterInit.init();
    }
    
}
