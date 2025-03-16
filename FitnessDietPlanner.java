import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*; // This imports java.util.List

// Model Classes
class Meal {
    int id;
    String name;
    int calories, protein;
    String ingredients, instructions;

    public Meal(int id, String name, int calories, int protein, String ingredients, String instructions) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public String toString() {
        return name + " (" + calories + " kcal, " + protein + " g protein)";
    }
}

class Workout {
    int id;
    String name;
    int duration;
    String muscleGroups;

    public Workout(int id, String name, int duration, String muscleGroups) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.muscleGroups = muscleGroups;
    }

    public String toString() {
        return name + " (" + duration + " min)";
    }
}

class Reminder {
    int id;
    String message;
    Timestamp time;

    public Reminder(int id, String message, Timestamp time) {
        this.id = id;
        this.message = message;
        this.time = time;
    }
}

class User {
    int id;
    String name;
    int age;
    String gender;
    double weight;
    double height;

    public User(int id, String name, int age, String gender, double weight, double height) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
    }

    public String toString() {
        return "Name: " + name + "\nAge: " + age + "\nGender: " + gender + "\nWeight: " + weight + " kg\nHeight: " + height + " cm";
    }
}

// Main Application
public class FitnessDietPlanner {
    private JFrame frame;
    private DefaultListModel<Meal> mealListModel;
    private DefaultListModel<Workout> workoutListModel;
    private JList<Meal> mealList;
    private JList<Workout> workoutList;
    private java.util.List<Reminder> reminders; // Explicitly use java.util.List
    private javax.swing.Timer timer; // Explicitly use javax.swing.Timer
    private User user;  // Store user information
    private JTextField nameField, ageField, genderField, weightField, heightField;

    private Connection conn;

    public FitnessDietPlanner() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection to the database
            String url = "jdbc:mysql://localhost:3306/userinfo";
            String dbUser = "root"; // Database username
            String dbPassword = ""; // Database password
            conn = DriverManager.getConnection(url, dbUser, dbPassword);
            System.out.println("Connected to the database!");

            // Initialize UI components
            frame = new JFrame("Fitness & Diet Planner");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 700);
            frame.setLayout(new GridBagLayout());
            frame.getContentPane().setBackground(new Color(240, 248, 255));

            mealListModel = new DefaultListModel<>();
            workoutListModel = new DefaultListModel<>();
            mealList = new JList<>(mealListModel);
            workoutList = new JList<>(workoutListModel);
            reminders = new ArrayList<>(); // Use java.util.ArrayList explicitly

            // Initialize Timer for reminders
            timer = new javax.swing.Timer(1000, e -> checkReminders()); // Timer triggers every second

            // User Info Panel
            JPanel userPanel = createUserInfoPanel();
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 0.2;
            gbc.insets = new Insets(10, 10, 0, 10);
            frame.add(userPanel, gbc);

            // Meal Panel
            JPanel mealPanel = createMealPanel();
            gbc.gridy = 1;
            gbc.insets = new Insets(0, 10, 0, 10);
            frame.add(mealPanel, gbc);

            // Workout Panel
            JPanel workoutPanel = createWorkoutPanel();
            gbc.gridy = 2;
            gbc.insets = new Insets(0, 10, 10, 10);
            frame.add(workoutPanel, gbc);

            // Reminder Panel
            JPanel reminderPanel = createReminderPanel();
            gbc.gridy = 3;
            gbc.weighty = 0.1;
            frame.add(reminderPanel, gbc);

            loadUserData();
            loadMeals();
            loadWorkouts();
            loadReminders();

            timer.start(); // Start the timer
            frame.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error connecting to the database: " + e.getMessage());
        }
    }

    private void checkReminders() {
        long currentTime = System.currentTimeMillis();
        Iterator<Reminder> iterator = reminders.iterator();
        while (iterator.hasNext()) {
            Reminder reminder = iterator.next();
            if (reminder.time.getTime() <= currentTime) {
                JOptionPane.showMessageDialog(frame, "Reminder: " + reminder.message);
                iterator.remove(); // Remove the reminder after it's triggered
            }
        }
    }

    private JPanel createUserInfoPanel() {
        JPanel userPanel = new JPanel(new GridBagLayout());
        userPanel.setBorder(BorderFactory.createTitledBorder("User Information"));
        userPanel.setBackground(new Color(112, 194, 219));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        userPanel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        userPanel.add(nameField, gbc);

        // Age
        gbc.gridx = 0;
        gbc.gridy = 1;
        userPanel.add(new JLabel("Age:"), gbc);
        ageField = new JTextField(20);
        gbc.gridx = 1;
        userPanel.add(ageField, gbc);

        // Gender
        gbc.gridx = 0;
        gbc.gridy = 2;
        userPanel.add(new JLabel("Gender:"), gbc);
        genderField = new JTextField(20);
        gbc.gridx = 1;
        userPanel.add(genderField, gbc);

        // Weight
        gbc.gridx = 0;
        gbc.gridy = 3;
        userPanel.add(new JLabel("Weight (kg):"), gbc);
        weightField = new JTextField(20);
        gbc.gridx = 1;
        userPanel.add(weightField, gbc);

        // Height
        gbc.gridx = 0;
        gbc.gridy = 4;
        userPanel.add(new JLabel("Height (cm):"), gbc);
        heightField = new JTextField(20);
        gbc.gridx = 1;
        userPanel.add(heightField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton setUserButton = createStyledButton("Save User Info");
        setUserButton.addActionListener(e -> saveUserInfo());
        buttonPanel.add(setUserButton);
        JButton newUserButton = createStyledButton("New User Info");
        newUserButton.addActionListener(e -> clearUserInfo());
        buttonPanel.add(newUserButton);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        userPanel.add(buttonPanel, gbc);

        return userPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 128, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        return button;
    }

    private JPanel createMealPanel() {
        JPanel mealPanel = new JPanel(new BorderLayout());
        mealPanel.setBorder(BorderFactory.createTitledBorder("Meal Plan"));
        mealPanel.setBackground(new Color(255, 250, 205));
        mealPanel.add(new JScrollPane(mealList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addMealButton = createStyledButton("Add Meal");
        addMealButton.addActionListener(e -> addMeal());
        buttonPanel.add(addMealButton);
        JButton removeMealButton = createStyledButton("Remove Meal");
        removeMealButton.addActionListener(e -> removeMeal());
        buttonPanel.add(removeMealButton);
        mealPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mealPanel;
    }

    private void removeMeal() {
        Meal selectedMeal = mealList.getSelectedValue();
        if (selectedMeal != null) {
            try {
                String sql = "DELETE FROM Meal WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, selectedMeal.id);
                pstmt.executeUpdate();
                mealListModel.removeElement(selectedMeal);
                JOptionPane.showMessageDialog(frame, "Meal removed successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Error removing meal: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a meal to remove.");
        }
    }

    private JPanel createWorkoutPanel() {
        JPanel workoutPanel = new JPanel(new BorderLayout());
        workoutPanel.setBorder(BorderFactory.createTitledBorder("Workout Plan"));
        workoutPanel.setBackground(new Color(255, 250, 205));
        workoutPanel.add(new JScrollPane(workoutList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addWorkoutButton = createStyledButton("Add Workout");
        addWorkoutButton.addActionListener(e -> addWorkout());
        buttonPanel.add(addWorkoutButton);
        JButton updateWorkoutButton = createStyledButton("Update Workout");
        updateWorkoutButton.addActionListener(e -> updateWorkout());
        buttonPanel.add(updateWorkoutButton);
        workoutPanel.add(buttonPanel, BorderLayout.SOUTH);

        return workoutPanel;
    }

    private void updateWorkout() {
        Workout selectedWorkout = workoutList.getSelectedValue();
        if (selectedWorkout != null) {
            String newName = JOptionPane.showInputDialog("Enter new name:", selectedWorkout.name);
            String newDurationStr = JOptionPane.showInputDialog("Enter new duration (minutes):", selectedWorkout.duration);
            String newMuscleGroups = JOptionPane.showInputDialog("Enter new muscle groups:", selectedWorkout.muscleGroups);
            try {
                int newDuration = Integer.parseInt(newDurationStr);
                if (newDuration <= 0) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid duration.");
                    return;
                }
                String sql = "UPDATE Workout SET name = ?, duration = ?, muscle_groups = ? WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newName);
                pstmt.setInt(2, newDuration);
                pstmt.setString(3, newMuscleGroups);
                pstmt.setInt(4, selectedWorkout.id);
                pstmt.executeUpdate();
                selectedWorkout.name = newName;
                selectedWorkout.duration = newDuration;
                selectedWorkout.muscleGroups = newMuscleGroups;
                workoutListModel.setElementAt(selectedWorkout, workoutList.getSelectedIndex());
                JOptionPane.showMessageDialog(frame, "Workout updated successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid number for duration.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Error updating workout: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a workout to update.");
        }
    }

    private JPanel createReminderPanel() {
        JPanel reminderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        reminderPanel.setBackground(new Color(152, 245, 249));
        JButton setReminderButton = createStyledButton("Set Reminder");
        setReminderButton.addActionListener(e -> setReminder());
        reminderPanel.add(setReminderButton);
        return reminderPanel;
    }

    private void saveUserInfo() {
        try {
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String gender = genderField.getText();
            double weight = Double.parseDouble(weightField.getText());
            double height = Double.parseDouble(heightField.getText());

            if (name.isEmpty() || age <= 0 || gender.isEmpty() || weight <= 0 || height <= 0) {
                JOptionPane.showMessageDialog(frame, "Please enter valid user information.");
                return;
            }

            String sql = "INSERT INTO UserInfo (name, age, gender, weight, height) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, gender);
            pstmt.setDouble(4, weight);
            pstmt.setDouble(5, height);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(frame, "User information saved successfully!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numbers for age, weight, and height.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error saving user information: " + e.getMessage());
        }
    }

    private void clearUserInfo() {
        nameField.setText("");
        ageField.setText("");
        genderField.setText("");
        weightField.setText("");
        heightField.setText("");
    }

    private void addMeal() {
        String name = JOptionPane.showInputDialog("Enter Meal Name:");
        String caloriesStr = JOptionPane.showInputDialog("Enter Calories:");
        String proteinStr = JOptionPane.showInputDialog("Enter Protein (g):");
        String ingredients = JOptionPane.showInputDialog("Enter Ingredients:");
        String instructions = JOptionPane.showInputDialog("Enter Instructions:");

        try {
            int calories = Integer.parseInt(caloriesStr);
            int protein = Integer.parseInt(proteinStr);

            if (calories <= 0 || protein < 0) {
                JOptionPane.showMessageDialog(frame, "Please enter valid nutritional information.");
                return;
            }

            String sql = "INSERT INTO Meal (name, calories, protein, ingredients, instructions) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setInt(2, calories);
            pstmt.setInt(3, protein);
            pstmt.setString(4, ingredients);
            pstmt.setString(5, instructions);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                Meal meal = new Meal(id, name, calories, protein, ingredients, instructions);
                mealListModel.addElement(meal);
                JOptionPane.showMessageDialog(frame, "Meal added successfully!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numbers for calories and protein.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error adding meal: " + e.getMessage());
        }
    }

    private void addWorkout() {
        String name = JOptionPane.showInputDialog("Enter Workout Name:");
        String durationStr = JOptionPane.showInputDialog("Enter Duration (minutes):");
        String muscleGroups = JOptionPane.showInputDialog("Enter Muscle Groups (e.g., Full Body):");

        try {
            int duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                JOptionPane.showMessageDialog(frame, "Please enter valid workout details.");
                return;
            }

            String sql = "INSERT INTO Workout (name, duration, muscle_groups) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setInt(2, duration);
            pstmt.setString(3, muscleGroups);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                Workout workout = new Workout(id, name, duration, muscleGroups);
                workoutListModel.addElement(workout);
                JOptionPane.showMessageDialog(frame, "Workout added successfully!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numbers for workout details.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error adding workout: " + e.getMessage());
        }
    }

    private void setReminder() {
        String message = JOptionPane.showInputDialog("Enter Reminder Message:");
        String timeStr = JOptionPane.showInputDialog("Enter time in seconds:");
        try {
            int time = Integer.parseInt(timeStr);
            Timestamp reminderTime = new Timestamp(System.currentTimeMillis() + (time * 1000));

            String sql = "INSERT INTO Reminder (message, time) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, message);
            pstmt.setTimestamp(2, reminderTime);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                Reminder reminder = new Reminder(id, message, reminderTime);
                reminders.add(reminder);
                JOptionPane.showMessageDialog(frame, "Reminder set successfully!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid time entered.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error setting reminder: " + e.getMessage());
        }
    }

    private void loadUserData() {
        try {
            String sql = "SELECT * FROM UserInfo ORDER BY id DESC LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String gender = rs.getString("gender");
                double weight = rs.getDouble("weight");
                double height = rs.getDouble("height");
                user = new User(id, name, age, gender, weight, height);

                nameField.setText(name);
                ageField.setText(String.valueOf(age));
                genderField.setText(gender);
                weightField.setText(String.valueOf(weight));
                heightField.setText(String.valueOf(height));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading user data: " + e.getMessage());
        }
    }

    private void loadMeals() {
        try {
            String sql = "SELECT * FROM Meal";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int calories = rs.getInt("calories");
                int protein = rs.getInt("protein");
                String ingredients = rs.getString("ingredients");
                String instructions = rs.getString("instructions");
                Meal meal = new Meal(id, name, calories, protein, ingredients, instructions);
                mealListModel.addElement(meal);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading meals: " + e.getMessage());
        }
    }

    private void loadWorkouts() {
        try {
            String sql = "SELECT * FROM Workout";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int duration = rs.getInt("duration");
                String muscleGroups = rs.getString("muscle_groups");
                Workout workout = new Workout(id, name, duration, muscleGroups);
                workoutListModel.addElement(workout);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading workouts: " + e.getMessage());
        }
    }

    private void loadReminders() {
        try {
            String sql = "SELECT * FROM Reminder";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String message = rs.getString("message");
                Timestamp time = rs.getTimestamp("time");
                Reminder reminder = new Reminder(id, message, time);
                reminders.add(reminder);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading reminders: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FitnessDietPlanner::new);
    }
}