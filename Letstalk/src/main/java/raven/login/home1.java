package raven.login;

import net.miginfocom.swing.MigLayout;
import raven.manager.DatabaseConnector;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.geom.Ellipse2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;


public class home1 extends JPanel {

    private JTextArea postTextArea;
    private JLabel uploadedImageLabel;
    private JLabel userLabel;
    private static JLabel profilePicLabel;
    private String loggedInEmail; 
    private JTextField searchField;
    private JButton searchButton;
    private Graph graph;


    public home1(String email, String loggedInEmail) {
        this.loggedInEmail = loggedInEmail; 
        this.graph = new Graph();
        init(email);
    }

    private void init(String email) {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[center][grow]"));

        JLabel label = new JLabel("Lets Talk !");
        label.setFont(new Font("Arial", Font.BOLD, 30));

        JPanel postPanel = createPostPanel();
        JPanel searchPanel = createSearchPanel();


        add(label, "wrap");
        add(postPanel, "grow, wrap");  // Wrap the postPanel to the next row
        add(searchPanel, "dock east, wrap ");
    }

    // ===================================Search panel code=================================================== 
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new MigLayout("fillx", "[grow]10[100px]10[100px]"));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField(20);
        searchButton = new JButton("Search");

        searchPanel.add(searchField, "span, grow");
        searchPanel.add(searchButton, "wrap");

        JPanel userPanel = createUserPanel("example@email.com");
        searchPanel.add(userPanel, "span, grow");


        return searchPanel;
    }
    
    private JPanel createPostPanel() {
        JPanel postPanel = new JPanel(new MigLayout("fillx, insets 5", "[grow]10[100px]10[100px]"));
        postPanel.setBackground(Color.WHITE);
        postPanel.setPreferredSize(new Dimension(400, 200));

        // Your existing code for the text area, upload button, post button, and image label
        postTextArea = new JTextArea(5, 20);
        postTextArea.setLineWrap(true);
        postTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(postTextArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JButton uploadButton = new JButton("Upload Image");
        uploadButton.addActionListener(new UploadButtonListener());

        JButton postButton = new JButton("Post");
        postButton.addActionListener(new PostButtonListener());

        uploadedImageLabel = new JLabel();
        uploadedImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        postPanel.add(scrollPane, "span, grow");
        postPanel.add(uploadButton);
        postPanel.add(postButton, "wrap");
        postPanel.add(uploadedImageLabel, "span, grow");

        return postPanel;
    }

    // Create a new post component
    private JPanel createPostComponent(String user_email, String text, byte[] imageData) {
        JPanel postComponent = new JPanel(new MigLayout("fillx, insets 5", "[80px]10[grow]"));
        postComponent.setBackground(Color.BLUE);

        // Add profile picture label
        JLabel profilePicLabel = createCircularProfilePicLabel(user_email);
        postComponent.add(profilePicLabel);

        // Add full name and username
        String fullName = fetchFullName(user_email);
        JLabel fullNameLabel = new JLabel("<html><b>" + fullName + "</b></html>");
        postComponent.add(fullNameLabel, "gapx 5");

        // Add text label
        JLabel textLabel = new JLabel("<html><i>" + text + "</i></html>");
        postComponent.add(textLabel, "wrap");

        // Add image label
        if (imageData != null) {
            ImageIcon imageIcon = new ImageIcon(imageData);
            JLabel imageLabel = new JLabel(imageIcon);
            postComponent.add(imageLabel, "span, grow");
        }

        // Add like, comment, and share buttons (adjust as needed)
        JButton likeButton = new JButton("Like");
        JButton commentButton = new JButton("Comment");
        JButton shareButton = new JButton("Share");
        postComponent.add(likeButton, "split 3, align left");
        postComponent.add(commentButton);
        postComponent.add(shareButton, "align right");

        return postComponent;
    }

    // Create a circular profile picture label
    private JLabel createCircularProfilePicLabel(String user_email) {
        // Fetch the profile picture from the database (adjust the query accordingly)
        byte[] profileImageData = fetchProfileImage(user_email);

        ImageIcon profileImageIcon = (profileImageData != null) ? new ImageIcon(profileImageData) : null;
        ImageIcon circularProfileImageIcon = createCircularIcon(profileImageIcon);

        JLabel profilePicLabel = new JLabel(circularProfileImageIcon);
        profilePicLabel.setPreferredSize(new Dimension(80, 80));
        profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        return profilePicLabel;
    }

    // Fetch the profile image from the database
    private byte[] fetchProfileImage(String user_email) {
        // Adjust the SQL query based on your database schema
        String query = "SELECT profile_image FROM users WHERE email = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user_email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBytes("profile_image");
                }
            }

        } catch (SQLException e) {
            DatabaseConnector.handleSQLException(e);
        }
        return null;
    }

    // Fetch full name from the database
    private String fetchFullName(String user_email) {
        // Adjust the SQL query based on your database schema
        String query = "SELECT full_name FROM users WHERE email = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user_email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("full_name");
                }
            }

        } catch (SQLException e) {
            DatabaseConnector.handleSQLException(e);
        }
        return "";
    }

    // Create a circular ImageIcon
    private ImageIcon createCircularIcon(ImageIcon originalIcon) {
        if (originalIcon == null) {
            return null;
        }

        int diameter = Math.min(originalIcon.getIconWidth(), originalIcon.getIconHeight());
        Image image = originalIcon.getImage();
        BufferedImage resizedImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();

        // Set a white background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, diameter, diameter);

        // Draw the original image (clipped to a circle)
        g2d.setClip(new Ellipse2D.Double(0, 0, diameter, diameter));
        g2d.drawImage(image, 0, 0, diameter, diameter, null);
        g2d.dispose();

        return new ImageIcon(resizedImage);
    }



        private class UploadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(home1.this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                displayImage(selectedFile);
                postTextArea.append("\nUploaded Image: " + selectedFile.getPath());
            }
        }
    }
    

    private void displayImage(File imageFile) {
        ImageIcon imageIcon = new ImageIcon(imageFile.getPath());
        Image image = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(image);
        uploadedImageLabel.setIcon(imageIcon);
    }


    private class PostButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        String postText = postTextArea.getText();
        ImageIcon icon = (ImageIcon) uploadedImageLabel.getIcon();
        byte[] imageData = (icon != null) ? convertImageToBytes(icon.getImage()) : null;
        DatabaseConnector.insertPost(loggedInEmail, postText, imageData);
        JOptionPane.showMessageDialog(home1.this, "Post successful", "Post Successful", JOptionPane.INFORMATION_MESSAGE);
        postTextArea.setText("");
        uploadedImageLabel.setIcon(null);
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
}
//============================================Users content"posting part" creation part finishes======================================================


//========================================Users porfile panel start =============================================================
    private JPanel createUserPanel(String email) {
        JPanel userPanel = new JPanel(new MigLayout("fillx, insets 5", "[grow]"));
        userPanel.setBackground(Color.YELLOW);
        userPanel.setPreferredSize(new Dimension(200, 300));
        
        userPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 5), 
                BorderFactory.createEmptyBorder(10, 10, 10, 10))); 
    
        
        profilePicLabel = new JLabel("");  
        profilePicLabel.setOpaque(true);
        profilePicLabel.setBackground(Color.WHITE);
        profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        profilePicLabel.setPreferredSize(new Dimension(80, 80)); 

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new EditButtonListener());

        userPanel.add(profilePicLabel, "wrap, split 2, gapx 10");
        userPanel.add(editButton,"alignx right");

        userLabel = new JLabel(fetchUserData(loggedInEmail));
        userPanel.add(userLabel, "wrap");

        JButton updateButton = new JButton("Update");
        updateButton.setBackground(Color.RED);
        updateButton.addActionListener(new UpdateButtonListener());
        userPanel.add(updateButton, "alignx right, gaptop 10");
    
        return userPanel;
    }    

    private class EditButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(home1.this);
    
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

    public class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (profilePicLabel.getIcon() != null) {
                ImageIcon icon = (ImageIcon) profilePicLabel.getIcon();
                Image image = icon.getImage();
                byte[] imageData = convertImageToBytes(image);
                updateUserImage(loggedInEmail, imageData);
                JOptionPane.showMessageDialog(home1.this, "Profile picture updated successfully", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(home1.this, "No image selected", "Update Failed", JOptionPane.WARNING_MESSAGE);
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

//========================================Users porfile panel finished  =============================================================
    


}
