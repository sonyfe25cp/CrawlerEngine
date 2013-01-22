package bit.crawl.extractor.classic.xpath;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

public class DBsink implements Sink {
	public String output;
	public Connection conn;
	
	public DBsink(){}
	
	@Override
	public void setOutput(String output){
		this.output = output;
		try {
			conn = DriverManager.getConnection(output);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error in url");
		}
	}
	
	@Override
	public void sink(HashMap<String, Object> input, String output) {
		// TODO Auto-generated method stub
		try {
			Connection newConn = DriverManager.getConnection(output);
			Connection tmp = conn;
			conn = newConn;
			sink(input);
			conn = tmp;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void sink(HashMap<String, Object> input) {
		if(input == null){
			System.out.println("input is null!");
			return;
		}
		
		try {
			Statement stmt = conn.createStatement();
			String query = "insert into table ";
			query += "";
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
