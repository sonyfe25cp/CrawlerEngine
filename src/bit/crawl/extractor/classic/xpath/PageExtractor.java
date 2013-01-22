package bit.crawl.extractor.classic.xpath;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.htmlcleaner.XPatherException;

import bit.crawl.extractor.sink.SqlSink;
import bit.crawl.extractor.source.PageStoreSource;
import bit.crawl.extractor.xpath.Expression;
import bit.crawl.store.PageStoreException;
import bit.crawl.store.PageStoreReader;
import bit.crawl.store.StoredPage;

import com.mysql.jdbc.Connection;

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

	private String urlPattern;
	Pattern p;
	protected String name;
	protected ArrayList<String> isKey;
	protected List<Expression> xPathExpressions;
	protected XPathFactory factory = XPathFactory.newInstance();

	/**
	 * Constructor.
	 * 
	 * @param siteName
	 *            site
	 * @param xPathExpressions
	 *            list of {@link Expression}
	 */
	public PageExtractor(String siteName, String urlPattern,
			List<Expression> xPathExpressions) {
		name = siteName;
		this.urlPattern = urlPattern;
		this.xPathExpressions = xPathExpressions;
		p = Pattern.compile(this.urlPattern);
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
		return xPathExpressions;
	}

	/**
	 * Set the extract rules. Rules are in the form of xpath
	 * 
	 * @param xPathExpressions
	 *            list of {@link Expression}
	 */
	public void setExtractRules(List<Expression> xPathExpressions) {
		this.xPathExpressions = xPathExpressions;
	}

	/**
	 * Add an extract rule into extractor. Rule is in the form of xpath.
	 * 
	 * @param xPathExpression
	 *            It contains string of the xpath and its corresponding name
	 */
	public void addExtractRule(Expression xPathExpression) {
		xPathExpressions.add(xPathExpression);
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
	 * A abstract mehod which should be implement in subclass to extract the
	 * page.
	 * 
	 * @param file
	 * @param charset
	 * @return columns' name and their values
	 * @throws XPatherException
	 *             if any errors occurs
	 * @throws IOException
	 *             if any errors occurs
	 */
	public abstract HashMap<String, String> extract(File file, String charset);

	/**
	 * Method to extract and store file.
	 * 
	 * @param fileName
	 *            file to be extracted
	 * @param conn
	 *            database connection
	 */
	public void extractAndStore(String fileName, Connection conn,
			ArrayList<String> tableKey) {
		PageStoreReader psReader = new PageStoreReader(new File(fileName));

		try {
			Statement stmt = conn.createStatement();
			SqlQueryGen sqlQueryGen = new SqlQueryGen(name);
			String query;
			ResultSet rs;
			HashMap<String, String> map;

			for (int i = 0;; i++) {
				// url fit?
				StoredPage sp = psReader.load();

				String url = sp.getHeader("URL");
				System.out.println(url);
				Matcher m = p.matcher(url);
				if (!m.matches()) {
					System.out.println("not match");
					continue;
				}

				map = extract(sp.getContent());

				// System.out.println(map);

				// select to assure of existance
				query = sqlQueryGen.genSelectQuery(map, tableKey);
				if (query != null) {
					rs = stmt.executeQuery(query);
					if (!rs.next()) {
						// insert
						query = sqlQueryGen.genInsertQuery(map);
						if (query != null)
							stmt.execute(query);
					}
				} else {
					System.out.println("query is null " + i);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PageStoreException e) {
			// End of file.
		}
	}

	public static void main(String args[]) {
//		PageExtractXML PEXML = new PageExtractXML("sina");
//		// PEXML.getUrlPattern();
//		PEXML.genOrAlterTable();
//
//		PageExtractor pe = new NewsExtractor("sina", PEXML.getUrlPattern(),
//				PEXML.getXPathExpression(PageType.news));
//		PageStoreSource pss = new PageStoreSource();
//		List<String> file = new ArrayList<String>();
//		file.add("/home/linss/workspace/sina-society_2010-11-19T17_34_00.pages");
//		pss.setFiles(file);
//		
//		
//		DataSource ds = (DataSource) PEXML.getDBConnection();
//		SqlSink ss = new SqlSink();
//		ss.setDataSource(ds);
//		ss.save(pe.extract(pss.readPage().getContent()), PEXML);

		/*
		 * PageExtractor pe = new NewsExtractor("sina", PEXML.getUrlPattern(),
		 * PEXML.getXPathExpression(PageType.news)); pe.extractAndStore(
		 * "/home/linss/workspace/sina-society_2010-11-19T17_34_00.pages",
		 * PEXML.getDBConnection(), PEXML.getTableKey()); /* PageStoreReader
		 * psReader = new PageStoreReader(new File(
		 * "sina-society_2010-11-19T17_34_00.pages")); ArrayList<StoredPage> sp
		 * = psReader.loadAll();
		 * 
		 * PageExtractXML PEXML = new PageExtractXML("sina");
		 * PEXML.genOrAlterTable(); PageExtractor pe = new NewsExtractor("sina",
		 * PEXML.getXPathExpression(PageType.news));
		 * 
		 * try { Statement stmt = PEXML.getDBConnection().createStatement();
		 * SqlQueryGen sqlQueryGen = new SqlQueryGen("sina"); String query; for
		 * (int i = 0; i < 2; i++) { query =
		 * sqlQueryGen.genInsertQuery(pe.extract(sp.get(i) .getContent()));
		 * stmt.execute(query); } } catch (SQLException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } /* PageExtractXML
		 * PEXML = new PageExtractXML("Sina"); PEXML.genOrAlterTable(); try {
		 * Statement stmt = PEXML.getDBConnection().createStatement(); String a
		 * = "insert into sina values(\"我\",\"2213\",\"1213\",\"1sq\");";
		 * stmt.execute(a); } catch (SQLException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 */
	}

}
