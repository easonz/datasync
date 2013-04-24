package org.zhangpan.datasync.db;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
  
  
public class DBConnection {  
  
    private static Connection conn = null;  
    private static Properties props = null;  
  
    static {  
        props = new Properties();  
        try {  
        	//InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("db-config.properties");
        	InputStream is = new FileInputStream("application.properties");
			props.load(is);
		} catch (Throwable e) {
			e.printStackTrace();
        }  
		try {
			if (StringUtils.isNotBlank(props.getProperty("jdbc.driver"))) {
				Class.forName(props.getProperty("jdbc.driver"));
			}
        } catch (ClassNotFoundException e) {  
            e.printStackTrace();  
        }  
    }  
      
  
    public static Connection getConn(){  
        try {  
			conn = DriverManager.getConnection(props.getProperty("jdbc.url"),
					props.getProperty("jdbc.username"),
					props.getProperty("jdbc.password"));
            conn.setAutoCommit(false);  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
        return conn;  
    }  
  
      
      
    public void closeConn(){  
        try {  
            if (conn != null)  
                conn.close();  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
          
    }  
}  
