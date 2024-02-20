package raven.component;

import net.miginfocom.swing.MigLayout;
import raven.manager.DatabaseConnector;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserPanel extends JPanel {
    private static JLabel profilePicLabel;
    private JLabel userLabel;
    private String loggedInEmail;

    public UserPanel(String email) {
        this.loggedInEmail = email;
        initUserPanel();
    }

    private void initUserPanel() {
        setLayout(new MigLayout("fillx, insets 5", "[grow]"));
        setBackground(Color.YELLOW);
        setPreferredSize(new Dimension(200, 300));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 5),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        profilePicLabel = new JLabel("");
        profilePicLabel.setOpaque(true);
        profilePicLabel.setBackground(Color.WHITE);
        profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        profilePicLabel.setPreferredSize(new Dimension(80, 80));

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new EditButtonListener());

        add(profilePicLabel, "wrap, split 2, gapx 10");
        add(editButton, "alignx right");

        userLabel = new JLabel(fetchUserData(loggedInEmail));
        add(userLabel, "wrap");

        JButton updateButton = new JButton("Update");
        updateButton.setBackground(Color.RED);
        updateButton.addActionListener(new UpdateButtonListener());
        add(updateButton, "alignx right, gaptop 10");
    }

    private class EditButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(UserPanel.this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                displayImageInProfilePicLabel(selectedFile);
            }
        }

        private void displayImageInProfilePicLabel(File imageFile) {
            ImageIcon imageIcon = new ImageIcon(imageFile.getPath());
            Image image = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(image);
            profilePicLabel.setIcon(imageIcon);
        }
    }

    private class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (profilePicLabel.getIcon() != null) {
                ImageIcon icon = (ImageIcon) profilePicLabel.getIcon();
                Image image = icon.getImage();
                byte[] imageData = convertImageToBytes(image);
                updateUserImage(loggedInEmail, imageData);
                JOptionPane.showMessageDialog(UserPanel.this, "Profile picture updated successfully", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(UserPanel.this, "No image selected", "Update Failed", JOptionPane.WARNING_MESSAGE);
            }
        }

        private byte[] convertImageToBytes(Image image) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(toBufferedImage(image), "png", baos);
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private BufferedImage toBufferedImage(Image image) {
            BufferedImage bufferedImage = new BufferedImage(
                    image.getWidth(null),
                    image.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
            return bufferedImage;
        }

        private void updateUserImage(String email, byte[] imageData) {
            String query = "UPDATE users SET profile_image = ? WHERE email = ?";

            try (Connection connection = DatabaseConnector.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setBytes(1, imageData);
                preparedStatement.setString(2, email);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Profile picture updated for user: " + email);
                } else {
                    System.out.println("Failed to update profile picture for user: " + email);
                }

            } catch (SQLException ex) {
                DatabaseConnector.handleSQLException(ex);
            }
        }
    }

    public static String fetchUserData(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
    
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setString(1, email);
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String fullName = resultSet.getString("full_name");
                    String gender = resultSet.getString("gender");
                    String dateOfBirth = resultSet.getString("date_of_birth");
                    String dateOfCreatedAccount = resultSet.getString("date_of_created_account");
    
                    byte[] imageData = resultSet.getBytes("profile_image");
                    ImageIcon imageIcon = (imageData != null) ? new ImageIcon(imageData) : null;
                    setProfileImage(imageIcon);
                    String userData = "<html><font color='black'>" +
                            "Username: " + username + "<br>" +
                            "Email: " + email + "<br>" +
                            "Full Name: " + fullName + "<br>" +
                            "Gender: " + gender + "<br>" +
                            "Date of Birth: " + dateOfBirth + "<br>" +
                            "Date of Created Account: " + dateOfCreatedAccount +
                            "</font></html>";
    
                    System.out.println("Debug: Fetched data - " + userData);
    
                    return userData;
                }
            }
    
        } catch (SQLException e) {
            DatabaseConnector.handleSQLException(e);
        }
    
        System.out.println("Debug: No data fetched for email: " + email);
        return null;
    }
    
    private static void setProfileImage(ImageIcon imageIcon) {
        profilePicLabel.setIcon(imageIcon);
    }
}

