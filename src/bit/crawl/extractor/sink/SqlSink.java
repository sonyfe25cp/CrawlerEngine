package bit.crawl.extractor.sink;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import bit.crawl.extractor.ExtractorSink;

public class SqlSink implements ExtractorSink {

	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Saves a document into a relational database. Only MySQL is tested.
	 * 
	 * @param document
	 *            The document. Must be Map<String,Object>.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void save(Object document, String dbName) {
		Map<String, Object> mapDoc;
		try {
			mapDoc = (Map<String, Object>) document;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException(
					"SqlSink only support Map<String, Object> documents.");
		}

		// Make sure the order of columns does not change.
		List<String> keys = new ArrayList<String>(mapDoc.keySet());
		String columns = StringUtils.join(keys, ',');
		String questionMarks = "?" + StringUtils.repeat(",?", keys.size() - 1);

		String sql = String.format("INSERT INTO %s (%s) VALUES (%s);", dbName,
				columns, questionMarks);
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);

			for (int i = 0; i < keys.size(); i++) {
				String key = keys.get(i);
				Object value = mapDoc.get(key);
				pstmt.setObject(i+1, value);
			}
			
			pstmt.execute();
		} catch (SQLException e) {
			throw new SqlSinkException("Error performing database operation.",
					e);
		}
	}
}
