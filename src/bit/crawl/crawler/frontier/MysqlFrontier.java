package bit.crawl.crawler.frontier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import bit.crawl.crawler.PageInfo;

public class MysqlFrontier {
	private String taskName;
	private DataSource dataSource;

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public PageInfo getNextPageToCrawl() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			PageInfo pageInfo = new PageInfo();

			conn = dataSource.getConnection();

			pstmt = conn.prepareStatement(""
					+ "SELECT id_pageinfo, url, http_status, etag,"
					+ "last_modified, last_crawled, "
					+ "last_responded, crawl_status, distance "
					+ "FROM pageinfo"
					+ "WHERE task_name = ? and dispatched = 0 "
					+ "ORDER BY distance ASC" + "LIMIT 1");
			
			pstmt.setString(1, taskName);
			pstmt.execute();
			
			rs = pstmt.getResultSet();
			
			while(rs.next()) {
				pageInfo.setUrl(rs.getString("url"));
				pageInfo.setHttpStatus(rs.getInt("http_status"));
				pageInfo.setEtag(rs.getString("etag"));
				pageInfo.setLastModified(rs.getDate("last_modified"));
				pageInfo.setLastResponded(rs.getDate("last_respond"));
				
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {

				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		return null;
	}

}
