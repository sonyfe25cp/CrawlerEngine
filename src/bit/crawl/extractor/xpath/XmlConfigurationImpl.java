package bit.crawl.extractor.xpath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlConfigurationImpl implements Configuration {
	static Logger logger = Logger.getLogger(XmlConfigurationImpl.class);

	XPath xpath = XPathFactory.newInstance().newXPath();
	XPathExpression xPathExpr;
	String _file;

	public String get_file() {
		return _file;
	}

	public void set_file(String file) {
		this._file = file;
	}

	public XmlConfigurationImpl(String file) {
		_file = file;
	}

	@Override
	public List<Expression> getConfig() {
		List<Expression> expr = new ArrayList<Expression>();

		File f = new File(_file);
		if (!f.exists()) {
			logger.info("Can't find file " + _file);
			throw new ConfigureFileNotFoundException(_file);
		}
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		docFactory.setNamespaceAware(true); // never forget this!
		try {
			Document doc = docFactory.newDocumentBuilder().parse(f);
			xPathExpr = xpath.compile("rule" + "/*/text()");
			NodeList nodes = (NodeList) xPathExpr.evaluate(doc,
					XPathConstants.NODESET);

			for (int i = 0; i < nodes.getLength(); i++) {
				logger.info("Xpath: "+nodes.item(i).getParentNode().getNodeName()
						+ " - " + nodes.item(i).getNodeValue());
				expr.add(new Expression(nodes.item(i).getParentNode()
						.getNodeName(), nodes.item(i).getNodeValue()));
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		logger.debug("get configuration: " + expr);
		return expr;
	}

}
