package bit.crawl.extractor.source.warc;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.entity.EntityDeserializer;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.io.HttpResponseParser;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

public class ArchiveHttpReader {
	private static final HttpParams DEFAULT_HTTP_PARAMS = new BasicHttpParams();
	private static final LineParser DEFAULT_LINE_PARSER = new BasicLineParser();
	private static final HttpResponseFactory DEFAULT_HTTP_RESPONSE_FACTORY = new DefaultHttpResponseFactory();

	private static final EntityDeserializer ENTITY_DESERIALIZER = new EntityDeserializer(new LaxContentLengthStrategy());
	
	public static HttpResponse readResponse(InputStream record) throws IOException, HttpException {
		InputStreamSessionInputBuffer issib = new InputStreamSessionInputBuffer(
				record, DEFAULT_HTTP_PARAMS);
		HttpResponseParser parser = new HttpResponseParser(issib,
				DEFAULT_LINE_PARSER, DEFAULT_HTTP_RESPONSE_FACTORY,
				DEFAULT_HTTP_PARAMS);

		HttpResponse result = (HttpResponse) parser.parse();
		
		HttpEntity entity = ENTITY_DESERIALIZER.deserialize(issib, result);
		
		Header contentEncodingHeader = entity.getContentEncoding();
		
		if(contentEncodingHeader != null && contentEncodingHeader.getValue().equals("gzip")) {
			entity = new GzipDecompressingEntity(entity);
		}
		
		result.setEntity(entity);
		

		return result;
	}
}
