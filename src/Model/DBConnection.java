package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Chandupa
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/attendancesys_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Chandupa@2022"; // Change this

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
