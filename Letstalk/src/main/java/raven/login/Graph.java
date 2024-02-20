package raven.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import raven.manager.DatabaseConnector;

public class Graph {
    private Map<String, List<String>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    public void addFriendToDatabase(String user1, String user2) {
        // Assuming you have a table named "friends" with columns "user1" and "user2"
        String query = "INSERT INTO friends (user1, user2) VALUES (?, ?)";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user1);
            preparedStatement.setString(2, user2);

            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            DatabaseConnector.handleSQLException(ex);
        }
    }

    public List<String> getFriendsFromDatabase(String user) {
        List<String> friends = new ArrayList<>();
        String query = "SELECT user2 FROM friends WHERE user1 = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    friends.add(resultSet.getString("user2"));
                }
            }

        } catch (SQLException ex) {
            DatabaseConnector.handleSQLException(ex);
        }

        return friends;
    }

    public List<String> getFriends(String user) {
        List<String> friends = getFriendsFromDatabase(user);
        adjacencyList.put(user, friends);
        return friends;
    }

    public void addFriend(String user1, String user2) {
        adjacencyList.computeIfAbsent(user1, k -> new ArrayList<>()).add(user2);
        adjacencyList.computeIfAbsent(user2, k -> new ArrayList<>()).add(user1);

        addFriendToDatabase(user1, user2);
    }


    private List<String> getContentTags(String user) {
        List<String> contentTags = new ArrayList<>();
        String query = "SELECT tags FROM posts WHERE user_email = ?";
    
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setString(1, user);
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Split tags and add to the list
                    String tags = resultSet.getString("tags");
                    if (tags != null && !tags.isEmpty()) {
                        contentTags.addAll(Arrays.asList(tags.split(",")));
                    }
                }
            }
    
        } catch (SQLException ex) {
            DatabaseConnector.handleSQLException(ex);
        }
    
        return contentTags;
    }

    private boolean hasContentWithTag(String user, String contentTag) {
        // Retrieve posts for the user from the database and check for the specified tag
        // Implement this method based on your database schema and structure
        // Adjust the SQL query according to your needs
        String query = "SELECT * FROM posts WHERE user_email = ? AND tags LIKE ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user);
            preparedStatement.setString(2, "%" + contentTag + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException ex) {
            DatabaseConnector.handleSQLException(ex);
        }

        return false;
    }
}
