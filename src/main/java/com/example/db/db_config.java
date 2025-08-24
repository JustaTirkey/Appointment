package db;
import java.sql.Connection;
import java.sql.DriverManager;

public class db_config {
    private static final String URL = "jdbc:mysql://localhost:3306/appointment_system";
    private static final String USER = "root";     // change if needed
    private static final String PASSWORD = "your password"; // add your password  // change if needed

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
