package Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static String url;
    private static String user;
    private static String password;
    
    static {
        // Try to load from installation directory first
        File configFile = new File("config.properties");
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                Properties props = new Properties();
                props.load(fis);
                url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/attendancesys_db");
                user = props.getProperty("db.user", "root");
                password = props.getProperty("db.password", "");
            } catch (IOException e) {
                // Fall through to defaults
            }
        }
        
        // Set defaults if not configured
        if (url == null) {
            url = "jdbc:mysql://localhost:3306/attendancesys_db";
            user = "root";
            password = "Chandupa@2022"; //Default password
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}