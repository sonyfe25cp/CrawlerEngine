package bit.crawl.pagesavers;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import bit.crawl.crawler.IPageSaver;
import bit.crawl.crawler.PageInfo;

public class FileSystemPageSaver implements IPageSaver {

	private String baseDir;

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	@Override
	public void savePage(PageInfo pageInfo) {
		String url = pageInfo.getUrl();

		File file = null;
		try {
			URI uri = new URI(url);
			String host = uri.getHost();
			File hostDir = new File(baseDir, host);
			String path = uri.getPath();
			if(path.endsWith("/")) {
				file = new File(hostDir, path + "index.html_nofilename");
			} else {
				file = new File(hostDir, path);
			}
		} catch (URISyntaxException e) {
			File storageDir = new File(baseDir, "unknown-hosts");
			file = new File(storageDir, "unknown:" + url.hashCode());
		}

		try {
			FileUtils.write(file, pageInfo.getContent());
		} catch (IOException e) {
			throw new RuntimeException("Cannot write file" + file, e);
		}

	}

}
