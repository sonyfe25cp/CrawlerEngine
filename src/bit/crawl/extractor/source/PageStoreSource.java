package bit.crawl.extractor.source;

import java.io.File;
import java.util.*;

import bit.crawl.extractor.FileBasedExtractorSource;
import bit.crawl.store.PageStoreException;
import bit.crawl.store.PageStoreReader;
import bit.crawl.store.StoredPage;
import bit.crawl.util.Logger;

public class PageStoreSource implements FileBasedExtractorSource {
	private static Logger logger = new Logger();

	private PageStoreReader reader;

	private List<String> files = new LinkedList<String>();

	public PageStoreReader getReader() {
		return reader;
	}

	public void setReader(PageStoreReader reader) {
		this.reader = reader;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files.clear();
		this.files.addAll(files);
	}

	public PageStoreSource() {
	}

	public PageStoreSource(PageStoreReader reader) {
		this.reader = reader;
	}

	@Override
	public void setPath(String path) {
		this.files.clear();
		this.files.add(path);
	}

	@Override
	public synchronized StoredPage readPage() {
		while (!files.isEmpty() || reader != null) {
			if (reader == null) {
				String nextFile = files.remove(0);
				logger.debug("Opening archive:" + nextFile);
				reader = new PageStoreReader(new File(nextFile));
			}
			StoredPage page = null;
			boolean archiveExhausted = false;
			try {
				page = reader.load();
				if (page == null) {
					archiveExhausted = true;
				}
			} catch (PageStoreException e) {
				archiveExhausted = true;
			}
			if (archiveExhausted) {
				logger.debug("Closing archive...");
				try {
					reader.close();
				} catch (Exception e2) {
				} finally {
					reader = null;
				}
			} else {
				return page;
			}
		}
		return null;
	}
}
