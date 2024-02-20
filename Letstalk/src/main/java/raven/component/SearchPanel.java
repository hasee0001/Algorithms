package raven.component;

import net.miginfocom.swing.MigLayout;
import raven.manager.DatabaseConnector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchPanel extends JPanel {

    private JTextField searchField;
    private JButton searchButton;

    public SearchPanel() {
        initSearchPanel();
    }

    private void initSearchPanel() {
        setLayout(new MigLayout("fillx", "[grow]10[100px]10[100px]"));
        setBackground(Color.WHITE);

        searchField = new JTextField(20);
        searchButton = new JButton("Search");

        searchButton.addActionListener(new SearchButtonListener());
        add(searchField, "span, grow");
        add(searchButton, "wrap");
    }

    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String usernameToSearch = searchField.getText();
            String searchResult = searchUserByUsername(usernameToSearch);

            if (searchResult != null) {
                // Display a message or take further actions if needed
                JOptionPane.showMessageDialog((Component) e.getSource(), searchResult, "User Found", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // User not found, show a message
                JOptionPane.showMessageDialog((Component) e.getSource(), "User Not Found", "User Not Found", JOptionPane.WARNING_MESSAGE);
            }
        }

        private String searchUserByUsername(String username) {
            String query = "SELECT * FROM users WHERE username = ?";

            try (Connection connection = DatabaseConnector.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return "<html><u>Username:</u> " + resultSet.getString("username") + "<br>" +
                                "<u>Email:</u> " + resultSet.getString("email") + "<br>" +
                                "<u>Full Name:</u> " + resultSet.getString("full_name") + "<br>" +
                                "<u>Gender:</u> " + resultSet.getString("gender") + "</html>";
                    }
                }

            } catch (SQLException ex) {
                DatabaseConnector.handleSQLException(ex);
            }

            return null;
        }
    }
}

