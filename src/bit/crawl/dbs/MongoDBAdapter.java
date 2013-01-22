package bit.crawl.dbs;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * This class is a adapter of mongoDB connection.
 * 
 * @since JDK1.6 mongo2.5
 */
public class MongoDBAdapter {
	private Mongo db;
	private DBCollection dbc;
	public MongoDBAdapter(){}
	/**
	 * Constructor.
	 * 
	 * @param dbname
	 *            name of database
	 * @param cname
	 *            name of collection
	 */
	public MongoDBAdapter(String dbname, String cname) {
		try {
			db = new Mongo();
			dbc = db.getDB(dbname).getCollection(cname);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param db
	 *            instance of Mongo
	 * @param dbname
	 *            name of database
	 * @param cname
	 *            name of collection
	 */
	public MongoDBAdapter(Mongo db, String dbname, String cname) {
		this.db = db;
		dbc = db.getDB(dbname).getCollection(cname);
	}
	
	/**
	 * 
	 */
	public ArrayList<Object> get(String key, String value) {
		ArrayList<Object> o = new ArrayList<Object>();
		DBCursor cursor = dbc.find(new BasicDBObject(key, value));
		while (cursor.hasNext()) {
			o.add(cursor.next());
		}
		return null;
	}

	public boolean put(String key, Serializable value) {
		dbc.insert(new BasicDBObject(key, value));
		return false;
	}

	public void dump() {
		dbc = null;
		db.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MongoDBAdapter a = new MongoDBAdapter();
		a.get("", "");
	}

}
