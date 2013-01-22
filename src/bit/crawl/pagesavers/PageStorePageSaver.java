package bit.crawl.pagesavers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import bit.crawl.crawler.IPageSaver;
import bit.crawl.crawler.PageInfo;
import bit.crawl.store.PageStoreWriter;
import bit.crawl.store.StoredPage;

public class PageStorePageSaver implements IPageSaver {
	private String taskName;
	private String baseDir;
	private PageStoreWriter writer = null;

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public PageStorePageSaver() {
		baseDir = "";
	}

	private synchronized void createWriter() {
		if (writer == null) {
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String fileName = String.format("%s_%s.pages", taskName,
					sdf.format(now));

			writer = new PageStoreWriter(new File(baseDir, fileName));
		}
	}

	@Override
	public void savePage(PageInfo pageInfo) {
		if (writer == null) {
			createWriter();
		}
		StoredPage sp = new StoredPage();
		sp.setHeader("URL", pageInfo.getUrl().toString());
		sp.setContent(pageInfo.getContent());
		writer.store(sp);
	}

	public void close() {
		if (writer != null) {
			writer.close();
		}
	}

}
