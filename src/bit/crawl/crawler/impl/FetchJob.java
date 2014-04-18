package bit.crawl.crawler.impl;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

import bit.crawl.crawler.CrawlAction;
import bit.crawl.crawler.Crawler;
import bit.crawl.crawler.FilterRule;
import bit.crawl.crawler.PageInfo;
import bit.crawl.util.Logger;

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
            extractLinks();// 抽取链接
            crawler.reportPageFetched(pageInfo);// 保存需要store的
            if (pageInfo.getCrawlFlag() == CrawlAction.FOLLOW) {
                logger.debug("url:" + url + " ----------------follow");
                crawler.reportLinks(pageInfo.getLinks(), distance, url);
            } else if (pageInfo.getCrawlFlag() == CrawlAction.STORE) {
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
        //		request.setHeader("Accept-Encoding", "gzip");
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
                && !contentType.matches("(application|text)/(xml|xhtml|html)(\\s*;.*)?")) {
            logger.error("Wrong content type: " + contentType);
            throw new WontFetchException();
        }

        HttpEntity entity = response.getEntity();
        try {
            String body = IOUtils.toString(entity.getContent(), crawler.getEncoding());
            pageInfo.setContent(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void extractLinks() throws Exception {
        logger.debug("Extracting links " + pageInfo.getUrl());
        String content = pageInfo.getContent();
        if (content == null || content.length() == 0) {
            return;
        }
        URI uri = new URI(pageInfo.getUrl());

        Parser parser = new Parser();
        parser.setInputHTML(content);
        NodeList nodeList = parser.parse(new TagNameFilter("A"));
        logger.debug("get links from " + pageInfo.getUrl() + " size : " + nodeList.size());
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.elementAt(i);

            LinkTag tag = (LinkTag) node;
            String linkHref = tag.extractLink();
            if (linkHref.indexOf("http") != linkHref.lastIndexOf("http")) {
                continue;
            }
            try{
                URI linkUri = uri.resolve(linkHref);
                String link = linkUri.toString();
                if(link != null && link.length() > 0){
                    for (FilterRule fr : crawler.getFilterRules()) {
                        CrawlAction ca = fr.judge(link);
                        if (ca == CrawlAction.STORE || ca == CrawlAction.FOLLOW || ca == CrawlAction.FOLLOW_STORE) {
                            logger.debug("linkUri : " + link +" -- ca : " + ca.toString());
                            pageInfo.getLinks().add(link);
                        }
                    }
                }
            }catch(Exception ignore){
            }
        }

    }

}
