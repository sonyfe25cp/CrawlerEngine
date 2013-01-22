package bit.crawl.extractor.xpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.htmlcleaner.XPatherException;

/**
 * This class is used to extract pages while given specific xpath expression in
 * the form of {@link Expression}. All of the critical fields come from
 * {@link PageExtractXML}. However, directly using this class is not suggested.
 * And its subclass should override method
 * {@link PageExtractor#extract(String, String)}.
 * 
 * @author lins
 * @version 1.0
 * @see Expression
 * @see PageExtractXML
 * @since 1.6
 */
public abstract class PageExtractor {
	static Logger logger = Logger.getLogger(PageExtractor.class);

	protected String name;
	protected boolean _toTrim = false;
	protected List<Expression> _xPathExprs;
	protected XPathFactory factory = XPathFactory.newInstance();

	/**
	 * Configure this extractor.
	 */
	public PageExtractor configWith(Configuration cfg) {
		_xPathExprs = cfg.getConfig();
		return this;
	}
	
	public PageExtractor trimResult( ){
		_toTrim = true;
		return this;
	}
	
	public PageExtractor notTrimResult( ){
		_toTrim = false;
		return this;
	}
	/**
	 * Get the name of the pages.
	 * 
	 * @return pages' name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the pages
	 * 
	 * @param name
	 *            pages' name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the current expression list.
	 * 
	 * @return the current expression list.
	 */
	public List<Expression> getXPathExpressions() {
		return _xPathExprs;
	}

	/**
	 * Set the extract rules. Rules are in the form of xpath
	 * 
	 * @param xPathExpressions
	 *            list of {@link Expression}
	 */
	public void setExtractRules(List<Expression> xPathExpressions) {
		this._xPathExprs = xPathExpressions;
	}

	/**
	 * Add an extract rule into extractor. Rule is in the form of xpath.
	 * 
	 * @param xPathExpression
	 *            It contains string of the xpath and its corresponding name
	 */
	public void addExtractRule(Expression xPathExpression) {
		_xPathExprs.add(xPathExpression);
	}

	/**
	 * A abstract mehod which should be implement in subclass to extract the
	 * page.
	 * 
	 * @param fileContent
	 *            content of the file
	 * @return columns' name and their values
	 * @throws XPatherException
	 *             if any errors occurs
	 * @throws IOException
	 *             if any errors occurs
	 */
	public abstract HashMap<String, String> extract(String fileContent);

	/**
	 * A mehod which should be implement in subclass to extract the page.
	 * 
	 * @param file
	 * @param charset
	 * @return columns' name and their values
	 * @throws XPatherException
	 *             if any errors occurs
	 * @throws IOException
	 *             if any errors occurs
	 */
	public HashMap<String, String> extract(File file, String charset) {
		HashMap<String, String> c2v = new HashMap<String, String>();
		try {
			c2v = extract(IOUtils.toString(new FileInputStream(file), charset));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return c2v;
	}

}
