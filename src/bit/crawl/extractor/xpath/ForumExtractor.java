package bit.crawl.extractor.xpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

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
	long _id;
	HtmlCleaner cleaner = new HtmlCleaner();

	public void resetId() {
		_id = 0;
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
		try {
			tagNode = cleaner.clean(file);

			Object[] nodes;
			String xPathExpression;
			xPathExpression = _xPathExprs.get(0).getExpression();
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
				for (int i = 1; i < _xPathExprs.size(); i++) {
					xPathExpression = _xPathExprs.get(i).getExpression();
					try {
						nodes = frameNode.evaluateXPath(xPathExpression);

						for (Object node : nodes) {
							TagNode n = (TagNode) node;
							String content = n.getText().toString();
							if (_toTrim)
								c2v.put(_xPathExprs.get(i).getName() + _id,
										content.replaceAll("&nbsp;", " ")
												.replace("\n", ""));
							else
								c2v.put(_xPathExprs.get(i).getName() + _id,
										content);
							logger.info("Extract result: "
									+ _xPathExprs.get(i).getName() + ":"
									+ content.replaceAll("&nbsp;", " ")
									.replace("\n", ""));
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
				_id++;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return c2v;
	}
}
