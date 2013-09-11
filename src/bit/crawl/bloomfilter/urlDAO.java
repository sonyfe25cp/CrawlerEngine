package bit.crawl.bloomfilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



public class urlDAO {
	
	public urlDAO() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
    }

    static Connection conn;
    static Connection conn0[] = new Connection[4];
    static PreparedStatement ps;
    static PreparedStatement ps0[] = new PreparedStatement[4];
    static ResultSet rs;
    static ArrayList<String> arr;
	
	//管理员登录验证
    public static ResultSet getUrl(){
      String psd=null;
      try {

          conn = DBPool.getConnection();
          ps = conn.prepareStatement("select url from newsgn_qq;");
          rs=ps.executeQuery();
//          if(rs.next()) {
//        	  psd=rs.getString(1);
//          }
//          else{
//        	  psd="";
//          }
      } catch(Exception e) {
          System.out.println(e);
          }finally {
      }
        return rs;
    }
}