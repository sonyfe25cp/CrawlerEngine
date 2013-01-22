package bit.crawl.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A filter rule specifies if an URL should be saved, crawled or completely
 * avoided.
 * 
 * @author Kunshan Wang
 * 
 */
public class FilterRule {

	private List<Pattern> patterns = new ArrayList<Pattern>();

	/**
	 * If true, this rule applies to those URLs that does not match the pattern.
	 */
	private boolean negative;

	/**
	 * The action to take to the matched URL.
	 */
	private CrawlAction action;

	public List<Pattern> getPatterns() {
		return patterns;
	}

	public void setPatterns(List<Pattern> patterns) {
		this.patterns = patterns;
	}

	public Pattern getPattern() {
		return patterns.get(0);
	}

	public void setPattern(Pattern pattern) {
		this.patterns.clear();
		this.patterns.add(pattern);
	}

	public boolean isNegative() {
		return negative;
	}

	public void setNegative(boolean negative) {
		this.negative = negative;
	}

	public CrawlAction getAction() {
		return action;
	}

	public void setAction(CrawlAction action) {
		this.action = action;
	}

	public FilterRule() {
		super();
		this.negative = false;
		this.action = CrawlAction.FOLLOW;
	}

	/**
	 * 
	 * @author Kunshan Wang
	 * 
	 * @param pattern
	 * @param negative
	 * @param action
	 * @deprecated Use bean property setting, instead.
	 */
	public FilterRule(Pattern pattern, boolean negative, CrawlAction action) {
		super();
		this.setPattern(pattern);
		this.negative = negative;
		this.action = action;
	}

	public CrawlAction judge(String url) {
		for (Pattern p : this.patterns) {
			if (p.matcher(url).matches()) {
				return this.action;
			}
		}
		return null;
	}
}
