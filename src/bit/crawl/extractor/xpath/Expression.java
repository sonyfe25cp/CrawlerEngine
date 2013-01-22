package bit.crawl.extractor.xpath;

/**
 * This class is used to contain xpath expressions and their names.
 * 
 * @author linss
 * @version 1.0
 * @since 1.6
 */
public class Expression {
	protected String expression;
	protected String name;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            name of the xpath expression. It should be the same as the
	 *            table in DB and Table.cname.
	 * @param expression
	 *            string of xpath expression.
	 */
	public Expression(String name, String expression) {
		this.expression = expression;
		this.name = name;
	}

	public Expression() {
	}

	/**
	 * Get the name of xpath expression.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the xpath expression.
	 * 
	 * @return xpath expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Set the name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set xpath expression
	 * 
	 * @param expression
	 *            String of xpath expression
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

}
