package bit.crawl.bloomfilter;


/* 数据访问组件 */

import java.io.*;
import java.util.*;
import java.sql.*;

public class DBPool{
    private static DBPool instance = null;

    //取得连接
    public static synchronized Connection getConnection() {
        if (instance == null){
            instance = new DBPool();
        }
        return instance._getConnection();
    }

    private DBPool(){
        super();
    }

    private  Connection _getConnection(){
        try{
            String sDBDriver  = null;
            String sConnection   = null;
            String sUser = null;
            String sPassword = null;

            Properties p = new Properties();
            InputStream is = getClass().getResourceAsStream("/db.properties");
            p.load(is);
            sDBDriver = p.getProperty("DBDriver",sDBDriver);
            sConnection = p.getProperty("Connection",sConnection);
            sUser = p.getProperty("User","");
            sPassword = p.getProperty("Password","");

            Properties pr = new Properties();
            pr.put("user",sUser);
            pr.put("password",sPassword);
            pr.put("characterEncoding", "utf-8");
            pr.put("useUnicode", "TRUE");
            
            Class.forName(sDBDriver).newInstance();
            return DriverManager.getConnection(sConnection,pr);
        }
        catch(Exception se){
            System.out.println(se);
            return null;
        }
    }

    //释放资源
    public static void dbClose(Connection conn,PreparedStatement ps,ResultSet rs)
    throws SQLException
    {
          rs.close();
          ps.close();
          conn.close();

      }
    }


