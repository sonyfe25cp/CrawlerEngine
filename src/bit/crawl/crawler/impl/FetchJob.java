package bit.crawl.crawler.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.htmlparser.util.ParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bit.crawl.crawler.CrawlAction;
import bit.crawl.crawler.Crawler;
import bit.crawl.crawler.FilterRule;
import bit.crawl.crawler.PageInfo;
import bit.crawl.util.Logger;
import bit.crawl.util.SlurpUtils;

public class FetchJob implements Runnable {
	private static final Logger logger = new Logger();
	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:26.0) Gecko/20100101 Firefox/26.0";
	private static final int MAX_READ_SIZE = 1048576;

	private Crawler crawler;
	private PageInfo pageInfo;
	private HttpClient httpClient = new DefaultHttpClient();

	public FetchJob(PageInfo pageInfo, Crawler crawler) {
		this.pageInfo = pageInfo;
		this.crawler = crawler;
	}

	public void run() {
		String url = pageInfo.getUrl();
		try {
			int distance = pageInfo.getDistance();
			logger.info("Crawling " + url.toString());
			download();
			decode();
			extractLinks();// 抽取链接
			crawler.reportPageFetched(pageInfo);// 保存需要store的
			if (pageInfo.getCrawlFlag() == CrawlAction.FOLLOW) {
				logger.debug("url:" + url + " ----------------follow");
				crawler.reportLinks(pageInfo.getLinks(), distance, url);
			} else if(pageInfo.getCrawlFlag() == CrawlAction.STORE){
				logger.debug("url:" + url + " ----------------store");
				crawler.reportLinks(pageInfo.getLinks(), distance + 1, url);
			}
		} catch (WontFetchException e) {
			logger.error("Won't fetch " + url);
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("Error crawling %s", e, url);
			e.printStackTrace();
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

			response = httpClient.execute(request);
		} catch (IOException e) {
			logger.error("Error communicating to server: " + pageInfo.getUrl());
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
			logger.error("Wrong content type: " + contentType);
			throw new WontFetchException();
		}

		HttpEntity entity = response.getEntity();
		GZIPInputStream content;
		try {
			content = new GZIPInputStream(entity.getContent());
		} catch (Exception e) {
			logger.error("Cannot open content stream: " + pageInfo.getUrl());
			throw new WontFetchException();
		}

		byte[] rawContent = null;
		try {
			rawContent = SlurpUtils
					.toByteArrayWithLimit(content, MAX_READ_SIZE);
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

	static final Pattern headerCharsetPattern = Pattern
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

	static Pattern scriptPattern = Pattern.compile("<script>.*</script>");

	String clean(String html) {
		html = html.replaceAll("<script>.*</script>", "");
		return html;
	}

	public void extractLinks() throws ParserException, URISyntaxException {
		logger.debug("Extracting links " + pageInfo.getUrl());
		String content = pageInfo.getContent();
//		URI uri = new URI(pageInfo.getUrl());

		// Parser parser = new Parser();
		// parser.setInputHTML(content);
		Document doc = Jsoup.parse(content);
		// doc.select("script").remove();
		// String html = doc.html();
		// if(html.contains("script")){
		// logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		// }
		Elements as = doc.select("a");
		for (Element a : as) {
			String link = a.attr("href");
			for (FilterRule fr : crawler.getFilterRules()) {
				CrawlAction ca = fr.judge(link);
				if (ca != CrawlAction.AVOID) {
					pageInfo.getLinks().add(link);
				}
			}
		}
		// NodeList nodeList = parser.parse(new TagNameFilter("A"));
		// logger.info("get links from " + pageInfo.getUrl()+" size : " +
		// nodeList.size());
		// for (int i = 0; i < nodeList.size(); i++) {
		// Node node = nodeList.elementAt(i);
		//
		// LinkTag tag = (LinkTag) node;
		// String linkHref = tag.extractLink();
		// try {
		// URI linkUri = uri.resolve(linkHref);
		// String link = linkUri.toString();

//		for (FilterRule fr : crawler.getFilterRules()) {
//			CrawlAction ca = fr.judge(link);
//			if (ca != CrawlAction.AVOID) {
//				pageInfo.getLinks().add(linkUri.toString());
//			}
//		}
		// } catch (Exception e) {
		// logger.error("extract error : {}", e);
		// }
		// }

	}

}