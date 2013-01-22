package bit.crawl.store;

import java.util.*;

public class StoredPage {
	private Map<String, String> headers = new LinkedHashMap<String, String>();
	private String content;

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getHeader(String field) {
		return headers.get(field);
	}

	public void setHeader(String field, String value) {
		this.headers.put(field, value);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Content-Length:");
		sb.append(content.length());
		sb.append('\n');
		
		for (Map.Entry<String, String> header : headers.entrySet()) {
			sb.append(header.getKey());
			sb.append(':');
			sb.append(header.getValue());
			sb.append('\n');
		}
		
		sb.append('\n');
		sb.append(content);
		
		return sb.toString();
	}
}
