package raven.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/letstalk";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "nothing";


    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
    
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if a matching user is found
            }
    
        } catch (SQLException e) {
            handleSQLException(e);
            return false; // Return false in case of an exception
        }
    }

    public static void insertUser(String username, String password, String email, String fullName, String gender) {
        // Check if the email already exists
        if (isEmailRegistered(email)) {
            System.out.println("Account with this email already exists. Registration failed.");
            return;
        }
    
        String query = "INSERT INTO users (username, password, email, full_name, gender, date_of_created_account) " +
                       "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
    
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, fullName);
            preparedStatement.setString(5, gender);
    
            // Execute the SQL statement
            int rowsAffected = preparedStatement.executeUpdate();
    
            if (rowsAffected > 0) {
                System.out.println("Data successfully inserted into the database!");
            } else {
                System.out.println("Failed to insert data into the database.");
            }
    
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    public static int insertPost(String user_email, String text, byte[] imageData) {
        String query = "INSERT INTO posts (user_email, text, image) VALUES (?, ?, ?)";
        
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
    
            preparedStatement.setString(1, user_email);
            preparedStatement.setString(2, text);
            preparedStatement.setBytes(3, imageData);
    
            int rowsAffected = preparedStatement.executeUpdate();
    
            if (rowsAffected > 0) {
                // Retrieve the generated keys (in this case, the post ID)
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int postId = generatedKeys.getInt(1);
                        System.out.println("Post successfully inserted into the database with ID: " + postId);
                        return postId;
                    } else {
                        System.out.println("Failed to retrieve generated keys for the inserted post.");
                    }
                }
            } else {
                System.out.println("Failed to insert post into the database.");
            }
    
        } catch (SQLException e) {
            handleSQLException(e);
        }
    
        // Return a default value (you may choose to throw an exception here instead)
        return -1;
    }
    
    
    private static boolean isEmailRegistered(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setString(1, email);
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
    
        } catch (SQLException e) {
            handleSQLException(e);
        }
    
        return false; // Return false in case of an exception
    }
    

    public static String fetchUserData(String email) {
        String query = "SELECT * FROM users WHERE email = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return "<html><u>Username:</u> " + resultSet.getString("username") + "<br>" +
                    "<u>Email:</u> " + resultSet.getString("email") + "<br>" +
                    "<u>Full Name:</u> " + resultSet.getString("full_name") + "<br>" +
                    "<u>Gender:</u> " + resultSet.getString("gender") + "</html>";
                }
            }

        } catch (SQLException e) {
            handleSQLException(e);
        }

        return null;
    }

    public static void handleSQLException(SQLException ex) {
        System.err.println("SQL Exception:");
        System.err.println("Error Code: " + ex.getErrorCode());
        System.err.println("SQL State: " + ex.getSQLState());
        System.err.println("Message: " + ex.getMessage());
        ex.printStackTrace();

        throw new RuntimeException("SQL Exception occurred. See console for details.", ex);
    }

    public static boolean validateUser(String username2, String password2) {
        throw new UnsupportedOperationException("Unimplemented method 'validateUser'");
    }
}