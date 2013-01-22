package bit.crawl.extractor.processor;

import java.util.Map;

import org.github.wks.jhql.Jhql;
import org.github.wks.jhql.query.Queryer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bit.crawl.extractor.ExtractorProcessor;
import bit.crawl.store.StoredPage;

public class JhqlProcessor implements ExtractorProcessor {
	private Queryer queryer = null;
	private Jhql jhql = new Jhql();
	private boolean stripScripts = true;

	public boolean isStripScripts() {
		return stripScripts;
	}

	public void setStripScripts(boolean stripScripts) {
		this.stripScripts = stripScripts;
	}

	public void setExpression(String expr) {
		queryer = jhql.makeQueryer(expr);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object extract(StoredPage page) {
		Node root = jhql.htmlToDom(page.getContent());

		if (stripScripts) {
			removeTags(root);
		}

		return jhql.queryHtml(queryer, root,
				(Map<String, Object>) (Object) page.getHeaders());
	}

	private static void removeTags(Node node) {
		boolean detach = false;
		if (node instanceof Element) {
			Element elem = (Element) node;
			if (elem.getTagName().equalsIgnoreCase("script")
					|| elem.getTagName().equalsIgnoreCase("style")) {
				detach = true;
			}
		}
		if (detach) {
			node.getParentNode().removeChild(node);
		} else {
			NodeList childs = node.getChildNodes();
			for (int i = 0; i < childs.getLength(); i++) {
				removeTags(childs.item(i));
			}

		}
	}
}
