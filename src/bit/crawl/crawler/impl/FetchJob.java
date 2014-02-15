package bit.crawl.crawler.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.regex.*;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import bit.crawl.crawler.CrawlAction;
import bit.crawl.crawler.PageInfo;
import bit.crawl.util.Logger;
import bit.crawl.util.SlurpUtils;

public class FetchJob implements Runnable {
	private static final Logger logger = new Logger();
	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:26.0) Gecko/20100101 Firefox/26.0";
	private static final int MAX_READ_SIZE = 1048576;

	private HttpClient httpClient;
	private ICrawlerForWorker crawler;
	private PageInfo pageInfo;

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public FetchJob(HttpClient httpClient, PageInfo pageInfo,
			ICrawlerForWorker crawler) {
		this.httpClient = httpClient;
		this.pageInfo = pageInfo;
		this.crawler = crawler;
	}

	public void run() {
		String url = pageInfo.getUrl();
		int distance = pageInfo.getDistance();

		logger.info("Crawling " + url.toString());
		try {
			download();
			if (Thread.interrupted()) {
				return;
			}
			decode();
			if (Thread.interrupted()) {
				return;
			}
			extractLinks();//抽取链接
			if (Thread.interrupted()) {
				return;
			}
			crawler.reportPageFetched(pageInfo);//保存需要store的
			if (Thread.interrupted()) {
				return;
			}
			if(pageInfo.getCrawlFlag() == CrawlAction.FOLLOW){
				logger.info("url:"+url+" ----------------follow");
				crawler.reportLinks(pageInfo.getLinks(), distance, url);
			}else{
				logger.info("url:"+url+" ----------------store");
				crawler.reportLinks(pageInfo.getLinks(), distance+1, url);
			}
		} catch (WontFetchException e) {
			logger.info("Won't fetch " + url);
		} catch (Exception e) {
			logger.error("Error crawling %s", e, url);
		} finally {
			crawler.reportJobDone();
		}
	}

	public void download() throws WontFetchException {
		logger.debug("Downloading %s", pageInfo.getUrl());
		HttpGet request = new HttpGet(pageInfo.getUrl());
		request.setHeader("User-Agent", USER_AGENT);
		request.setHeader("Referer", pageInfo.getReferURL());
		request.setHeader("Accept-Encoding", "gzip");
		request.setHeader("Connection", "keep-alive");

		HttpResponse response;
		try {
			
			response = getHttpClient().execute(request);
		} catch (IOException e) {
			logger.info("Error communicating to server: " + pageInfo.getUrl());
			throw new WontFetchException();
		}
		int statusCode = response.getStatusLine().getStatusCode();
		pageInfo.setHttpStatus(statusCode);
		if (statusCode == HttpStatus.SC_NOT_MODIFIED) {
			
		} else if (statusCode != HttpStatus.SC_OK) {
			
		}
		for (Header header : response.getAllHeaders()) {
			String name = header.getName();
			String value = header.getValue();
			pageInfo.getHeaders().put(name, value);
		}

		String contentType = pageInfo.getHeaders().get("Content-Type");
		if (contentType != null
				&& !contentType
						.matches("(application|text)/(xml|xhtml|html)(\\s*;.*)?")) {
			logger.info("Wrong content type: " + contentType);
			throw new WontFetchException();
		}

		HttpEntity entity = response.getEntity();
		InputStream content;
		try {
			content = entity.getContent();
		} catch (Exception e) {
			logger.info("Cannot open content stream: " + pageInfo.getUrl());
			throw new WontFetchException();
		}

		byte[] rawContent = null;
		try {
			rawContent = SlurpUtils.toByteArrayWithLimit(content, MAX_READ_SIZE);
			EntityUtils.consume(entity);
		} catch (IOException e) {
			// pass silently.
		} finally {
			org.apache.commons.io.IOUtils.closeQuietly(content);
		}
		pageInfo.setRawContent(rawContent);
	}

	private static final Charset ISO88591CHARSET = Charset
			.forName("ISO-8859-1");
	private static final Pattern[] charsetPatterns = new Pattern[] {
			Pattern.compile(
					"<meta.*?charset\\s*=\\s*['\"]?([a-zA-Z0-9_\\-]+)['\"]?.*?>",
					Pattern.CASE_INSENSITIVE),
			Pattern.compile(
					"<\\?xml.*?encoding\\s*=\\s*['\"]?([a-zA-Z0-9_\\-]+)['\"]?.*?\\?>",
					Pattern.CASE_INSENSITIVE), };

	private static final Pattern headerCharsetPattern = Pattern
			.compile("charset=([a-zA-Z0-9_\\-]+)");

	public void decode() {
		logger.debug("Decoding " + pageInfo.getUrl());
		byte[] rawContent = pageInfo.getRawContent();
		String simpleDecode = new String(rawContent, ISO88591CHARSET);

		Charset pageCharset = null;

		for (Pattern charsetPattern : charsetPatterns) {
			Matcher m = charsetPattern.matcher(simpleDecode);
			boolean found = m.find();
			if (found) {
				String pageCharsetName = m.group(1);
				logger.debug("Charset pattern found in HTML. charset is [%s]",
						pageCharsetName);
				pageCharset = Charset.forName(pageCharsetName);
			}
		}

		if (pageCharset == null) {
			String contentType = pageInfo.getHeaders().get("Content-Type");
			if (contentType != null) {
				Matcher m = headerCharsetPattern.matcher(simpleDecode);
				boolean found = m.find();
				if (found) {
					String headerCharsetName = m.group(1);
					logger.debug(
							"Charset pattern found in header. charset is [%s]",
							headerCharsetName);
					pageCharset = Charset.forName(headerCharsetName);
				}
			}
		}

		if (pageCharset == null) {
			logger.debug("Charset pattern not found. Use default.");
			pageCharset = crawler.getCharset();
		}

		String content = new String(rawContent, pageCharset);
		pageInfo.setContent(content);

	}

	public void extractLinks() throws ParserException, URISyntaxException {
		logger.debug("Extracting links " + pageInfo.getUrl());
		String content = pageInfo.getContent();
		URI uri = new URI(pageInfo.getUrl());

		Parser parser = new Parser();
		parser.setInputHTML(content);
		NodeList nodeList = parser.parse(new TagNameFilter("A"));

		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.elementAt(i);

			LinkTag tag = (LinkTag) node;
			String linkHref = tag.extractLink();
			try {
				URI linkUri = uri.resolve(linkHref);
				pageInfo.getLinks().add(linkUri.toString());
			} catch (Exception e) {
				
			}
		}

	}

}