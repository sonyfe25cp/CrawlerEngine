package bit.crawl.extractor.xpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Node;

/**
 * Subclass of {@link PageExtractor}. And this class is dedicated to news.
 * Strategy pattern is used here. Method
 * {@link PageExtractor#extract(String file, String charset)} should be override
 * in this class.
 * 
 * @author linss
 * @version 1.0
 * @see PageExtractor
 * @since 1.6
 */
public class NewsExtractor extends PageExtractor {

	HtmlCleaner cleaner = new HtmlCleaner();

	/*
	 * public HashMap<String, String> extract(String file) { HashMap<String,
	 * String> c2v = new HashMap<String, String>();
	 * 
	 * TagNode tagNode; try { tagNode = cleaner.clean(file); // / Node rootNode
	 * = new // DomSerializer(cleaner.getProperties()).createDOM(tagNode);
	 * 
	 * Object[] nodes; for (int i = 0; i < xPathExpressions.size(); i++) {
	 * String xPathExpression = xPathExpressions.get(i) .getExpression(); try {
	 * nodes = tagNode.evaluateXPath(xPathExpression);
	 * 
	 * for (Object node : nodes) { TagNode n = (TagNode) node; if
	 * (c2v.containsKey(xPathExpressions.get(i).getName()))
	 * c2v.put(xPathExpressions.get(i).getName(),
	 * c2v.get(xPathExpressions.get(i).getName()) + n.getText().toString());
	 * else c2v.put(xPathExpressions.get(i).getName(), n .getText().toString());
	 * // System.out.println(xPathExpressions.get(i).getName() // + ":" +
	 * n.getText()); } } catch (NullPointerException e) {
	 * System.out.println("ERROR: XPathExpression is " + xPathExpression); }
	 * catch (XPatherException e) { // TODO Auto-generated catch block
	 * System.out.println("ERROR: XPathExpression is " + xPathExpression);
	 * e.printStackTrace(); } } } catch (IOException e1) { // TODO
	 * Auto-generated catch block e1.printStackTrace(); } return c2v; }
	 */

	private HashMap<String, XPath> xPathCache = new HashMap<String, XPath>();
	private DomSerializer domSerializer = new DomSerializer(
			cleaner.getProperties());

	/**
	 * Extract the page whose type is news.
	 * 
	 * @param file
	 *            content of the file to be extracted
	 * @throws XPatherException
	 *             if any errors occurs
	 * @throws IOException
	 *             if any errors occurs
	 * @see bit.crawl.extractor.classic.xpath.PageExtractor#extract(java.lang.String,
	 *      java.lang.String)
	 */
	public HashMap<String, String> extract(String file) {
		// using jaxen
		HashMap<String, String> c2v = new HashMap<String, String>();

		TagNode tagNode;
		try {
			tagNode = cleaner.clean(file);

			Node rootNode = domSerializer.createDOM(tagNode);

			List nodes;
			for (int i = 0; i < _xPathExprs.size(); i++) {
				String xPathExpression = _xPathExprs.get(i).getExpression();
				XPath xpath = xPathCache.get(xPathExpression);
				if (xpath == null) {
					xpath = new DOMXPath(xPathExpression);
					xPathCache.put(xPathExpression, xpath);
				}
				try {
					nodes = xpath.selectNodes(rootNode);

					for (Object node : nodes) {
						Node n = (Node) node;
						String content;
						if (c2v.containsKey(_xPathExprs.get(i).getName()))
							content = c2v.get(_xPathExprs.get(i).getName())
									+ n.getTextContent().toString();
						else
							content = n.getTextContent().toString();
						if (_toTrim)
							c2v.put(_xPathExprs.get(i).getName(),
									StringUtils.trim(content));
						else
							c2v.put(_xPathExprs.get(i).getName(),content);
						logger.info("Extract result: "
								+ _xPathExprs.get(i).getName() + " - "
								+ n.getTextContent());
					}
				} catch (NullPointerException e) {
					System.out.println("ERROR: XPathExpression is "
							+ xPathExpression);
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JaxenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c2v;
	}

}
