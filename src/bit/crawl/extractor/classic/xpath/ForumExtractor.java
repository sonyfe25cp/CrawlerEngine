package bit.crawl.extractor.classic.xpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import bit.crawl.extractor.xpath.Expression;

/**
 * Subclass of {@link PageExtractor}. And this class is dedicated to forum.
 * Strategy pattern is used here. Method
 * {@link PageExtractor#extract(String file, String charset)} should be override
 * in this class.
 * 
 * @author linss
 * @version 1.0
 * @see PageExtractor
 * @since 1.6
 */
public class ForumExtractor extends PageExtractor {

	HtmlCleaner cleaner = new HtmlCleaner();
	/**
	 * Constructor.
	 * 
	 * @param xPathExpressions
	 *            list of xpath expressions
	 */
	public ForumExtractor(String siteName, String urlPattern, List<Expression> xPathExpressions) {
		super(siteName, urlPattern, xPathExpressions);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Extract the page whose type is forum.
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
		HashMap<String, String> c2v = new HashMap<String, String>();

		TagNode tagNode;
			tagNode = cleaner.clean(file);

			Object[] nodes;
			String xPathExpression;
			xPathExpression = xPathExpressions.get(0).getExpression();
			Object[] frames = null;
			TagNode frameNode;
			try {
				frames = tagNode.evaluateXPath(xPathExpression);
			} catch (XPatherException e1) {
				// TODO Auto-generated catch block
				System.out.println(xPathExpression);
				e1.printStackTrace();
			}
			// System.out.println(frames[0].toString());
			for (Object frame : frames) {
				// System.out.println("12819281937");
				frameNode = (TagNode) frame;
				for (int i = 1; i < xPathExpressions.size(); i++) {
					xPathExpression = xPathExpressions.get(i).getExpression();
					try {
						nodes = frameNode.evaluateXPath(xPathExpression);

						for (Object node : nodes) {
							TagNode n = (TagNode) node;
							c2v.put(xPathExpressions.get(i).getName(), n
									.getText().toString());
							System.out.println(xPathExpressions.get(i)
									.getName() + ":" + n.getText());
						}
					} catch (NullPointerException e) {
						System.out.println("ERROR: XPathExpression is "
								+ xPathExpression);
					} catch (XPatherException e) {
						System.out.println("XPather ERROR: XPathExpression is "
								+ xPathExpression);
						e.printStackTrace();
					}
				}
			}
		return c2v;
	}

	/**
	 * Extract the page whose type is forum.
	 * 
	 * @param file
	 *            the file to be extracted
	 * @param charset
	 *            charset of the file
	 * @throws FileNotFoundException
	 *             if any errors occurs
	 * @throws IOException
	 *             if any errors occurs
	 * @see bit.crawl.extractor.classic.xpath.PageExtractor#extract(java.lang.String,
	 *      java.lang.String)
	 */
	public HashMap<String, String> extract(File file, String charset) {

		HashMap<String, String> c2v = new HashMap<String, String>();

		try {
			c2v = extract(IOUtils.toString(new FileInputStream(file), charset));

		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return c2v;
	}

	public static void main(String args[]) {
	}
}
