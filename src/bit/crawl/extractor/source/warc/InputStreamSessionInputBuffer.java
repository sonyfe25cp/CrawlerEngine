package bit.crawl.extractor.source.warc;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.impl.io.AbstractSessionInputBuffer;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

public class InputStreamSessionInputBuffer extends AbstractSessionInputBuffer {

	private final InputStream inputStream;
	private static final int DEFAULT_BUFFER_SIZE = 4096;

	public InputStream getInputStream() {
		return inputStream;
	}

	public InputStreamSessionInputBuffer(InputStream inputStream) {
		this.inputStream = inputStream;
		this.init(inputStream, DEFAULT_BUFFER_SIZE, new BasicHttpParams());
	}

	public InputStreamSessionInputBuffer(InputStream inputStream, HttpParams httpParams) {
		this.inputStream = inputStream;
		this.init(inputStream, DEFAULT_BUFFER_SIZE, httpParams);
	}

	@Override
	public boolean isDataAvailable(int timeout) throws IOException {
		return true;
	}

}
