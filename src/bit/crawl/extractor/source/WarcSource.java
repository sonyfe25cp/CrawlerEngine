package bit.crawl.extractor.source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveRecord;
import org.archive.io.warc.WARCReaderFactory;

import bit.crawl.extractor.ExtractorSource;
import bit.crawl.extractor.source.warc.ArchiveHttpReader;
import bit.crawl.store.StoredPage;
import bit.crawl.util.EncodingGuesser;
import bit.crawl.util.Logger;

public class WarcSource implements ExtractorSource {
	private static Logger logger = new Logger();

	private ArchiveReader archiveReader = null;
	private Iterator<ArchiveRecord> archiveReaderIterator = null;

	private Pattern warcMimetype = null;
	private Pattern httpMimetype = null;

	public void setPath(String path) throws MalformedURLException, IOException {
		archiveReader = WARCReaderFactory.get(path);
		archiveReaderIterator = archiveReader.iterator();
	}

	public void setWarcMimetype(String pattern) {
		warcMimetype = Pattern.compile(pattern);
	}

	public void setHttpMimetype(String pattern) {
		httpMimetype = Pattern.compile(pattern);
	}

	{
		setWarcMimetype("application/http;\\s+msgtype=response");
		setHttpMimetype(".*(html|xml|xhtml).*");
	}

	public WarcSource() {
	}

	public WarcSource(String path) throws MalformedURLException, IOException {
		this.setPath(path);
	}

	@Override
	public StoredPage readPage() {
		try {
			ArchiveRecord rec;
			while (true) {
				if (!archiveReaderIterator.hasNext()) {
					return null;
				}

				rec = archiveReaderIterator.next();

				String url = rec.getHeader().getUrl();

				String wm = rec.getHeader().getMimetype();

				if (wm == null || !warcMimetype.matcher(wm).matches()) {
					logger.debug("Bad WARC Mimetype '%s' for %s", wm, url);
					continue;
				}

				HttpResponse response;
				try {
					response = ArchiveHttpReader.readResponse(rec);
				} catch (HttpException e) {
					logger.debug("Bad HTTP record: %s", url);
					continue;
				}

				HttpEntity entity = response.getEntity();
				if (entity == null) {
					logger.debug("Null entity: %s", url);
					continue;
				}

				String hm = EntityUtils.getContentMimeType(entity);
				if (hm != null && !httpMimetype.matcher(hm).matches()) {
					logger.debug("Bad HTTP Mimetype '%s' for %s", hm, url);
					continue;
				}

				byte[] contentBytes = IOUtils.toByteArray(entity.getContent());

				Charset charset = EncodingGuesser.guessWithEncodingName(
						contentBytes, EntityUtils.getContentCharSet(entity),
						EncodingGuesser.UTF8CHARSET);

				String content = new String(contentBytes, charset);

				StoredPage sp = new StoredPage();
				sp.setHeader("URL", url);
				sp.setContent(content);
				return sp;
			}
		} catch (IOException e) {
			throw new RuntimeException("Error reading WARC archive: "
					+ archiveReader.getFileName(), e);
		}
	}
}
