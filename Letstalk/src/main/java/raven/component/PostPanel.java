package raven.component;

import net.miginfocom.swing.MigLayout;
import raven.manager.DatabaseConnector;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostPanel extends JPanel {

    private JTextArea postTextArea;
    private JLabel uploadedImageLabel;
    private String loggedInEmail;

    public PostPanel(String loggedInEmail) {
        this.loggedInEmail = loggedInEmail;
        initPostPanel();
    }

    private void initPostPanel() {
        setLayout(new MigLayout("fillx, insets 5", "[grow]10[100px]10[100px]"));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(400, 200));

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

        add(scrollPane, "span, grow");
        add(uploadButton);
        add(postButton, "wrap");
        add(uploadedImageLabel, "span, grow");

        // Fetch and display existing posts
        fetchAndDisplayPosts();
    }

    private void fetchAndDisplayPosts() {
        // Fetch posts from the database ordered by like_count
        String query = "SELECT * FROM posts ORDER BY like_count DESC";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String user_email = resultSet.getString("user_email");
                String text = resultSet.getString("text");
                byte[] imageData = resultSet.getBytes("image");

                add(createPostComponent(user_email, text, imageData, resultSet.getInt("id")), "wrap");
            }

        } catch (SQLException e) {
            DatabaseConnector.handleSQLException(e);
        }
    }

    private JPanel createPostComponent(String user_email, String text, byte[] imageData, int postId) {
        JPanel postComponent = new JPanel(new MigLayout("fillx, insets 5", "[80px]10[grow]"));
        postComponent.setBackground(Color.BLUE);

        JLabel profilePicLabel = createCircularProfilePicLabel(user_email);
        postComponent.add(profilePicLabel);

        String fullName = fetchFullName(user_email);
        String username = fetchUsername(user_email);
        JLabel fullNameLabel = new JLabel("<html><b>" + fullName + "</b></html>");
        JLabel usernameLabel = new JLabel(username);
        postComponent.add(fullNameLabel, "gapx 5");
        postComponent.add(usernameLabel, "wrap");

        JLabel textLabel = new JLabel("<html><i>" + text + "</i></html>");
        postComponent.add(textLabel, "wrap");

        if (imageData != null) {
            ImageIcon imageIcon = new ImageIcon(imageData);
            JLabel imageLabel = new JLabel(imageIcon);
            postComponent.add(imageLabel, "span, grow");
        }

        int likeCount = fetchLikeCount(postId);
        JButton likeButton = new JButton("Like");
        JButton commentButton = new JButton("Comment");
        JButton shareButton = new JButton("Share");
        likeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                likePost(loggedInEmail, postId);
                fetchAndDisplayPosts(); // Refresh the posts after liking
                likeButton.setText("Like (" + likeCount + ")");
            }
        });
        postComponent.add(likeButton, "split 3, align left");
        postComponent.add(commentButton);
        postComponent.add(shareButton, "align right");

        return postComponent;
    }

    private int fetchLikeCount(int postId) {
        String query = "SELECT like_count FROM posts WHERE id = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setInt(1, postId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("like_count");
                }
            }
    
        } catch (SQLException e) {
            DatabaseConnector.handleSQLException(e);
        }
        return 0;
    }

    private JLabel createCircularProfilePicLabel(String user_email) {
        byte[] profileImageData = fetchProfileImage(user_email);

        ImageIcon profileImageIcon = (profileImageData != null) ? new ImageIcon(profileImageData) : null;
        ImageIcon circularProfileImageIcon = createCircularIcon(profileImageIcon);

        JLabel profilePicLabel = new JLabel(circularProfileImageIcon);
        profilePicLabel.setPreferredSize(new Dimension(80, 80));
        profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        return profilePicLabel;
    }

    private byte[] fetchProfileImage(String user_email) {
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

    private String fetchFullName(String user_email) {
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

    private String fetchUsername(String user_email) {
        String query = "SELECT username FROM users WHERE email = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user_email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("username");
                }
            }

        } catch (SQLException e) {
            DatabaseConnector.handleSQLException(e);
        }
        return "";
    }

    private ImageIcon createCircularIcon(ImageIcon originalIcon) {
        if (originalIcon == null) {
            return null;
        }

        int diameter = Math.min(originalIcon.getIconWidth(), originalIcon.getIconHeight());
        Image image = originalIcon.getImage();
        BufferedImage resizedImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, diameter, diameter);

        g2d.setClip(new Ellipse2D.Double(0, 0, diameter, diameter));
        g2d.drawImage(image, 0, 0, diameter, diameter, null);
        g2d.dispose();

        return new ImageIcon(resizedImage);
    }

    private void likePost(String userEmail, int postID) {
        String query = "UPDATE posts SET like_count = like_count + 1 WHERE id = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, postID);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            DatabaseConnector.handleSQLException(e);
        }
    }

    private class UploadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog((Component) e.getSource());

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

            // Use SwingWorker for background database operation
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // Insert post data into the database
                    DatabaseConnector.insertPost(loggedInEmail, postText, imageData);
                    return null;
                }

                @Override
                protected void done() {
                    // Update UI in real-time after posting
                    removePostsComponents(); // Remove only the components displaying posts
                    fetchAndDisplayPosts(); // Fetch and display updated posts
                    revalidate();
                    repaint();
                    JOptionPane.showMessageDialog((Component) e.getSource(), "Post successful", "Post Successful", JOptionPane.INFORMATION_MESSAGE);
                    postTextArea.setText("");
                    uploadedImageLabel.setIcon(null);
                }
            };

            worker.execute(); // Execute the SwingWorker
        }
    }

    // Add a method to remove only the components that display posts
    private void removePostsComponents() {
        // Iterate through the components and remove only those displaying posts
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getBackground() == Color.BLUE) {
                remove(component);
            }
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
}
