package bit.crawl.extractor.classic.xpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class is used to generate SQL query.
 * 
 * @author linss
 * @version 1.0
 * @since 1.6
 */
public class SqlQueryGen {
	public String tableName;

	/**
	 * Constructor to initial tableName.
	 * 
	 * @param tableName
	 *            table name in DB
	 */
	public SqlQueryGen(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Reset table name.
	 * 
	 * @param tableName
	 *            table name to reset
	 */
	public void resetTableName(String tableName) {
		this.tableName = tableName;
	}

	public String genSelectQuery(HashMap<String, String> c2v,
			ArrayList<String> keys) {
		if (c2v.size() == 0)
			return null;
		
		String query = "select * from " + tableName + " where ";
		
		for (String key : keys) {
			if (!c2v.containsKey(key))
				return null;
			query += key;
			query += " = \"";
			String tmp = c2v.get(key);
			tmp = tmp.replace('\"', '“');
			query += tmp;
			query += "\" and ";
		}
		query = query.substring(0, query.length() - 5);
		query += ';';

		System.out.println(query);
		return query;
	}

	/**
	 * Generate insert query.
	 * 
	 * @param c
	 *            list of column names
	 * @param v
	 *            list of value
	 * @return insert query string
	 */
	public String genInsertQuery(HashMap<String, String> c2v) {
		if (c2v.size() == 0)
			return null;

		int i;
		String query = "insert into " + tableName + " (";

		Iterator it = c2v.entrySet().iterator();
		Map.Entry e;
		while (it.hasNext()) {
			e = (Entry) it.next();
			query += e.getKey();
			query += ',';
		}
		query = query.substring(0, query.length() - 1);
		query += ')';
		query += " values (";
		it = c2v.entrySet().iterator();
		while (it.hasNext()) {
			e = (Entry) it.next();
			query += "\"";
			String tmp = (String) e.getValue();
			tmp = tmp.replace('\"', '“');
			query += tmp;
			query += "\"";
			query += ',';
		}
		query = query.substring(0, query.length() - 1);
		query += ");";
		System.out.println(query);
		return query;
	}
}
