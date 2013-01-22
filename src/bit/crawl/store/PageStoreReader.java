package bit.crawl.store;

import java.io.*;
import java.util.*;

import org.apache.commons.io.IOUtils;

/**
 * Read stored pages.
 * 
 * @author Kunshan Wang
 * 
 */
public class PageStoreReader {
	private static final String DEFAULT_ENCODING = "UTF-8";
	private BufferedReader reader;

	/**
	 * Constructor with a given file.
	 * 
	 * @author Kunshan Wang
	 * 
	 * @param file
	 *            The file to read.
	 */
	public PageStoreReader(File file) {
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), DEFAULT_ENCODING));
		} catch (Exception e) {
			throw new PageStoreException(e);
		}
	}

	/**
	 * Constructor with a given BufferedReader.
	 * <p>
	 * 
	 * @author Kunshan Wang
	 * 
	 * @param reader
	 */
	public PageStoreReader(BufferedReader reader) {
		this.reader = reader;
	}

	public StoredPage load() {
		try {
			StoredPage page = new StoredPage();

			Integer contentLength = null;

			boolean firstLineRead = false;

			HEADER_READING: while (true) {
				String line;
				line = reader.readLine();
				if (line == null) {
					if (firstLineRead == false) {
						return null;
					} else {
						throw new PageStoreException("Unexpected end of file.");
					}
				}
				if (line.isEmpty()) {
					break HEADER_READING;
				}
				String[] kv = line.split(":", 2);
				String key = kv[0];
				String value = kv[1];

				if (key.equals("Content-Length")) {
					contentLength = new Integer(value);
				} else {
					page.setHeader(key, value);
				}
			}

			if (contentLength == null) {
				throw new PageStoreException("Missing content length");
			}

			char[] contentBuffer = new char[contentLength];
			int actuallyRead = reader.read(contentBuffer, 0, contentLength);

			if (actuallyRead < contentLength) {
				throw new PageStoreException("Unexpected end of file.");
			}

			page.setContent(new String(contentBuffer));

			return page;
		} catch (Exception e) {
			throw new PageStoreException(e);
		}
	}
	
	public ArrayList<StoredPage> loadAll() {
		return loadAll(true);
	}

	public ArrayList<StoredPage> loadAll(boolean tolerateBadEnding) {
		ArrayList<StoredPage> pages = new ArrayList<StoredPage>();
		try {
			while (true) {
				StoredPage page = load();
				if (page == null) {
					break;
				}
				pages.add(page);
			}
		} catch (PageStoreException e) {
			// Unexpected End of File
			if (!tolerateBadEnding) {
				throw e;
			}
		} catch (Exception e) {
			throw new PageStoreException(e);
		}
		return pages;
	}

	public void close() {
		IOUtils.closeQuietly(reader);
	}
}
