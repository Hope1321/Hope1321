import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateDatabase {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/"; // No database name here
        String user = "root"; // Default MySQL user in XAMPP
        String password = ""; // Default is empty in XAMPP

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to MySQL server!");

            // Create a statement
            Statement stmt = conn.createStatement();

            // SQL to create database
            String sql = "CREATE DATABASE IF NOT EXISTS userinfo";
            stmt.executeUpdate(sql);
            System.out.println("Database 'userinfo' created successfully!");

            // Close connection
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.err.println("Error creating database: " + e.getMessage());
        }
    }
}