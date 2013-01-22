package bit.crawl.extractor.classic.xpath;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import bit.crawl.extractor.xpath.Expression;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.Statement;

/**
 * This class is used for interpreting XML file like SinaExtract.xml, which
 * contains DB information and xpath expressoins.
 * 
 * @author linss
 * @version 1.0
 * @since 1.6
 */
public class PageExtractXML {
	private Document doc;
	private Connection con = null;
	private ArrayList<String> keys = null;
	XPathExpression expr;

	XPathFactory factory = XPathFactory.newInstance();
	XPath xpath = factory.newXPath();

	/**
	 * Constructor. It should determine which xml file to interpret.
	 * 
	 * @param site
	 *            site + "Extract.xml" = xml file.
	 * @throws ParserConfigurationException
	 *             if any errors occur
	 * @throws SAXException
	 *             if any errors occur
	 * @throws IOException
	 *             if any errors occur
	 */
	public PageExtractXML(String site) {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder;
		try {
			builder = domFactory.newDocumentBuilder();
			doc = builder.parse(site + "Extract.xml");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Change the site which corresponds to one xml. Re-initial variable con and
	 * expr.
	 * 
	 * @param site
	 *            site + "Extract.xml" = xml file.
	 * @throws ParserConfigurationException
	 *             if any errors occur
	 * @throws SAXException
	 *             if any errors occur
	 * @throws IOException
	 *             if any errors occur
	 */
	public void setSite(String site) {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder;
		try {
			builder = domFactory.newDocumentBuilder();
			doc = builder.parse(site + "Extract.xml");
			keys = null;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		con = null;
		expr = null;
	}
	
	/**
	 * Get URL pattern. URL pattern is the regular expression of the sites to be extractor.
	 * @return URL pattern
	 * @throws XPathExpressionException
	 * 				if any errors occur
	 */
	public String getUrlPattern(){
		String urlPattern = null;
		try {
			expr = xpath.compile("root/urlPattern/text()");
			NodeList nodes = (NodeList) expr.evaluate(doc,
					XPathConstants.NODESET);
			urlPattern = nodes.item(0).getNodeValue();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(urlPattern);
		return urlPattern;
	}
	
	/**
	 * Get the java DB connection whose URL, user and password is specified in
	 * the xml file. Here DB should be mysql. If variable con isn't null, return
	 * con directly.
	 * 
	 * @return an instance of {@link com.mysql.jdbc.Connection}.
	 * @throws SQLException
	 *             if any errors occur
	 * @throws XPathExpressionException
	 *             if any errors occur
	 */
	public Connection getDBConnection() {
		if (con != null)
			return con;
		try {
			expr = xpath.compile("root/db/*/text()");
			NodeList nodes = (NodeList) expr.evaluate(doc,
					XPathConstants.NODESET);
			String url = null, usr = null, pw = null, charset = null;
			for (int i = 0; i < nodes.getLength(); i++) {
				if (nodes.item(i).getParentNode().getNodeName().equals("url"))
					url = nodes.item(i).getNodeValue();
				else {
					if (nodes.item(i).getParentNode().getNodeName()
							.equals("user"))
						usr = nodes.item(i).getNodeValue();
					else {
						if (nodes.item(i).getParentNode().getNodeName()
								.equals("password"))
							pw = nodes.item(i).getNodeValue();
						else {
							if (nodes.item(i).getParentNode().getNodeName()
									.equals("charset"))
								charset = nodes.item(i).getNodeValue();
						}
					}
				}
			}
			System.out.println(url + "?user=" + usr + "&password=" + pw + "&useUnicode=true"
					+ "&characterEncoding=" + charset);

			con = (Connection) DriverManager.getConnection(url + "?user=" + usr
					+ "&password=" + pw + "&useUnicode=true"+ "&characterEncoding=" + charset);
			return con;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * return key of the table.
	 */
	public ArrayList<String> getTableKey(){
		return keys;
	}
	
	/**
	 * Generate the table specified in XML when it dosen't exist, or alter it
	 * when table in DB isn't equal to ours. Now only add column, drop column
	 * and modify type is available in altering table.
	 * 
	 * @throws SQLException
	 *             if any errors occur
	 * @throws XPathExpressionException
	 *             if any errors occur
	 */
	public void genOrAlterTable() {
		try {
			expr = xpath.compile("root/db/table/name/text()");
			NodeList nodes = (NodeList) expr.evaluate(doc,
					XPathConstants.NODESET);
			String tableName = nodes.item(0).getNodeValue();

			expr = xpath.compile("root/db/charset/text()");
			nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			String charset = nodes.item(0).getNodeValue();
			System.out.println(charset);

			DatabaseMetaData dbMetaData;
			dbMetaData = (DatabaseMetaData) getDBConnection().getMetaData();
			Statement stmt = (Statement) con.createStatement();
			ResultSet rs1 = dbMetaData.getTables(null, null, tableName, null);
			ResultSet rs2 = dbMetaData.getColumns(null, null, tableName, "%");

			HashMap<String, String> c2t = new HashMap<String, String>();
			// HashMap<String, String> c2k = new HashMap<String, String>();
			ArrayList isKey = new ArrayList<String>();
			keys = new ArrayList<String>();
			
			expr = xpath.compile("root/db/table/column/*/text()");
			nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i = i + 3) {
				c2t.put(nodes.item(i).getNodeValue(), nodes.item(i + 1)
						.getNodeValue());
				isKey.add(nodes.item(i + 2).getNodeValue());
				if(nodes.item(i + 2).getNodeValue().equals("yes")){
					keys.add(nodes.item(i).getNodeValue());
				}
			}
			
			
			
			if (rs1.next()) {
				// System.out.println("alter table or do nothing");

				//HashMap<String, String> c2e = new HashMap<String, String>();
				String tmp;

				while (rs2.next()) {
					// System.out.println(rs2.getString(4));
					// System.out.println(rs2.getString(6));
					// System.out.println(rs2.getString(7));
					if (!c2t.containsKey(rs2.getString(4))) {
						// when a column in db dosen't exist in xml, drop it

						stmt.execute("ALTER TABLE " + tableName
								+ " DROP COLUMN " + rs2.getString(4));
					} else {
						// a column is both in db and xml

						tmp = c2t.get(rs2.getString(4)).toUpperCase();
						if (!tmp.startsWith(rs2.getString(6))) {
							// type of a column in db id different from that in
							// xml
							/*
							 * System.out.println(rs2.getString(4));
							 * System.out.println(rs2.getString(6));
							 * System.out.println(c2t.get(rs2.getString(4)));
							 */
							stmt.execute("ALTER TABLE " + tableName
									+ " modify " + rs2.getString(4) + ' ' + tmp
									+ ";");

						} else {
							if (!tmp.equals(rs2.getString(6))) {
								// size of a column in db id different from that
								// in
								// xml
								if (!tmp.subSequence(
										rs2.getString(6).length() + 1,
										tmp.length() - 1).equals(
										rs2.getString(7))) {
									stmt.execute("ALTER TABLE "
											+ tableName
											+ " modify "
											+ rs2.getString(4)
											+ ' '
											+ rs2.getString(6)
											+ tmp.subSequence(rs2.getString(6)
													.length(), tmp.length())
											+ ";");
								}

							}
						}
						c2t.remove(rs2.getString(4));
					}
				}

				// add a column when it exits in xml not db
				Iterator it = c2t.entrySet().iterator();
				Map.Entry e;
				while (it.hasNext()) {
					e = (Map.Entry) it.next();
					System.out.println("ALTER TABLE " + tableName
							+ " ADD COLUMN " + e.getKey() + ' ' + e.getValue()
							+ ';');
					stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN "
							+ e.getKey() + ' ' + e.getValue() + ';');
				}

			} else {
				// System.out.println("create table");

				Iterator it = c2t.entrySet().iterator();
				Map.Entry e;
				String query = "create table " + tableName + '(';
				int i = isKey.size() - 1;
				while (it.hasNext()) {
					e = (Map.Entry) it.next();
					query += e.getKey();
					query += ' ';
					query += e.getValue();
					// System.out.println(isKey.get(i));
					if (isKey.get(i).equals("yes"))
						query += " key";
					if (i != 0)
						query += ',';
					i--;
				}
				query += ");";
				stmt.execute(query);
				// System.out.println(query);
			}
			// System.out.println("ALTER TABLE " + tableName +
			// " DEFAULT CHARACTER SET "
			// + charset + "; ");
			// stmt.execute("ALTER TABLE " + tableName +
			// " DEFAULT CHARACTER SET "
			// + charset + "; ");
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Get the list of xpath expression in the form of {@link Expression}. Here
	 * page type should be determined, including forum, news.
	 * 
	 * @param pageType
	 *            type of the page. Different types have different xpath
	 *            expression.
	 * @return list of xpath expression
	 * @throws XPathExpressionException
	 *             if any errors occur
	 * @see PageType
	 * @see Expression
	 */
	public List<Expression> getXPathExpression(PageType pageType) {
		List<Expression> xPathExpressions = new ArrayList<Expression>();

		try {
			expr = xpath.compile("root/rule/rule1/" + pageType + "/*/text()");
			NodeList nodes = (NodeList) expr.evaluate(doc,
					XPathConstants.NODESET);

			for (int i = 0; i < nodes.getLength(); i++) {
				System.out.println(nodes.item(i).getParentNode().getNodeName()
						+ ":" + nodes.item(i).getNodeValue());
				xPathExpressions.add(new Expression(nodes.item(i)
						.getParentNode().getNodeName(), nodes.item(i)
						.getNodeValue()));
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return xPathExpressions;
	}

	/**
	 * Get the list of xpath expression in the form of {@link Expression}. Here
	 * whether contents are duplicated should be determined.
	 * 
	 * @param isDuplicated
	 *            tables, forums are duplicated; news isn't.
	 * @return list of xpath expression
	 * @throws XPathExpressionException
	 *             if any errors occur
	 * @see Expression
	 * @deprecated Replaced by {@link PageExtractXML#getXPathExpression()}.
	 */
	public List<Expression> getXPathExpression(boolean isDuplicated) {
		List<Expression> xPathExpressions = new ArrayList<Expression>();

		try {
			if (isDuplicated)
				expr = xpath
						.compile("root/rule/rule1/isDuplicated[@value='1']/detail/*/text()");
			else
				expr = xpath
						.compile("root/rule/rule1/isDuplicated[@value='0']/detail/*/text()");
			NodeList nodes = (NodeList) expr.evaluate(doc,
					XPathConstants.NODESET);

			for (int i = 0; i < nodes.getLength(); i++) {
				System.out.println(nodes.item(i).getParentNode().getNodeName()
						+ ":" + nodes.item(i).getNodeValue());
				xPathExpressions.add(new Expression(nodes.item(i)
						.getParentNode().getNodeName(), nodes.item(i)
						.getNodeValue()));
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return xPathExpressions;
	}

	public static void main(String args[]) {
		PageExtractXML peXML = new PageExtractXML("Sina");
		peXML.getXPathExpression(PageType.forum);
		peXML.genOrAlterTable();
	}
}
