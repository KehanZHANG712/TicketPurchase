package util;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://hk-cdb-29mv0y7d.sql.tencentcdb.com:63928/swen90007?user=root", "root", "swen90007");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

