package bit.crawl.crawler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.sql.DataSource;

public class CrawlHistory {
	
	private DataSource dataSource;
	private String tableName;
	private int bufferSize;
	
	private Set<String> historySet = Collections.synchronizedSet(new HashSet<String>());
	private Set<String> bufferSet = Collections.synchronizedSet(new HashSet<String>());
	
	private Connection conn = null;
	private Statement stmt = null;
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	public Set<String> getHistorySet() {
		return historySet;
	}

	public void setHistorySet(Set<String> historySet) {
		this.historySet.clear();
		this.historySet = historySet;
	}

	public Set<String> getBufferSet() {
		return bufferSet;
	}

	public void setBufferSet(Set<String> bufferSet) {
		this.bufferSet.clear();
		this.bufferSet = bufferSet;
	}

	public void addToBufferSet(String value) {
		bufferSet.add(value);
	}
	
	private void createTable() throws SQLException
	{
		stmt.executeUpdate("CREATE TABLE " + tableName + 
				"(id int(11) auto_increment not null primary key, url varchar(1024) not null);");
	}
	
	private boolean checkTableExistence() throws SQLException
	{
		ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null);
		if(rs.next()) {
			return true;
		}
		return false;
	}
	
	/**
	 * load crawling history from database to the historySet
	 */
	public void loadHistory()
	{		
		try {
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			
			if(checkTableExistence() == false)
				createTable();
			else
			{
				ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
				while(rs.next())
				{
					historySet.add(rs.getString("url"));
				}
				rs.close();
				stmt.close();
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * add new crawling result in bufferSet to the database
	 */
	public synchronized void addHistory()
	{
		try {
			Iterator<String> iterator = bufferSet.iterator();
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			while (iterator.hasNext()) 
			{
				String url = iterator.next();
				stmt.executeUpdate("INSERT INTO " + tableName + " (url) VALUES ('" + url + "')");
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bufferSet.clear();
	}
}
