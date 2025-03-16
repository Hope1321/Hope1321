import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateTable {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/userinfo"; // Database URL
        String user = "root"; // Database username
        String password = ""; // Database password

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection to the database
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();

            // SQL statements to create tables
            String createUserTable = "CREATE TABLE IF NOT EXISTS UserInfo (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "age INT NOT NULL, " +
                    "gender VARCHAR(50), " +
                    "weight DOUBLE NOT NULL, " +
                    "height DOUBLE NOT NULL" +
                    ")";

            String createMealTable = "CREATE TABLE IF NOT EXISTS Meal (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "calories INT NOT NULL, " +
                    "protein INT NOT NULL, " +
                    "ingredients TEXT, " +
                    "instructions TEXT" +
                    ")";

            String createWorkoutTable = "CREATE TABLE IF NOT EXISTS Workout (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "duration INT NOT NULL, " +
                    "muscle_groups VARCHAR(255)" +
                    ")";

            String createReminderTable = "CREATE TABLE IF NOT EXISTS Reminder (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "message TEXT, " +
                    "time DATETIME" +
                    ")";

            // Execute SQL statements to create tables
            stmt.executeUpdate(createUserTable);
            System.out.println("Table UserInfo created successfully!");
            stmt.executeUpdate(createMealTable);
            System.out.println("Table Meal created successfully!");
            stmt.executeUpdate(createWorkoutTable);
            System.out.println("Table Workout created successfully!");
            stmt.executeUpdate(createReminderTable);
            System.out.println("Table Reminder created successfully!");

            // Close the statement and connection
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }
}