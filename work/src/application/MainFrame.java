
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import component.CustomProgressBar;
import component.RoundedButton;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.net.HttpURLConnection;

public class MainFrame extends JFrame {
    private JTextField urlTextField;
    private CustomProgressBar progressBar;
    private DefaultListModel<String> imageListModel;
    private JList<String> imageList;
    private ExecutorService threadPool;
    private Future<Void> downloadTask;
    private JLabel imageLabel;

    public MainFrame() {
        initializeUI();
        threadPool = Executors.newFixedThreadPool(5);
    }

    private void initializeUI() {
        setTitle("Image Downloader");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 500));

        ImageIcon appIcon = new ImageIcon(getClass().getResource("/images/Logo.png"));
        Image resizedIcon = appIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        setIconImage(resizedIcon);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        imageLabel = new JLabel("", SwingConstants.CENTER);
        JScrollPane imageScrollPane = new JScrollPane(imageLabel);
        mainPanel.add(imageScrollPane, BorderLayout.CENTER);

        JPanel downloadPanel = new JPanel(new BorderLayout());
        urlTextField = new JTextField("Paste URL here...");
        RoundedButton downloadButton = new RoundedButton("Download");
        RoundedButton pauseButton = new RoundedButton("Pause");
        RoundedButton resumeButton = new RoundedButton("Resume");
        progressBar = new CustomProgressBar();
        progressBar.setStringPainted(true);

        downloadButton.setBackground(new Color(53, 94, 59));
        downloadButton.setForeground(Color.WHITE);

        downloadButton.addActionListener(e -> {
            if (downloadTask == null || downloadTask.isDone() || downloadTask.isCancelled()) {
                startDownload(urlTextField.getText().trim());
            } else {
                JOptionPane.showMessageDialog(this, "Download in progress. Pause before starting a new download.");
            }
        });

        pauseButton.addActionListener(e -> pauseDownload());
        resumeButton.addActionListener(e -> resumeDownload());

        JPanel urlPanel = new JPanel(new BorderLayout());
        urlPanel.add(urlTextField, BorderLayout.CENTER);
        urlPanel.add(downloadButton, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);

        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(buttonPanel, BorderLayout.EAST);

        downloadPanel.add(urlPanel, BorderLayout.NORTH);
        downloadPanel.add(progressPanel, BorderLayout.CENTER);

        mainPanel.add(downloadPanel, BorderLayout.NORTH);

        imageListModel = new DefaultListModel<>();
        imageList = new JList<>(imageListModel);
        JScrollPane listScrollPane = new JScrollPane(imageList);
        listScrollPane.setPreferredSize(new Dimension(200, 0));
        mainPanel.add(listScrollPane, BorderLayout.EAST);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startDownload(String url) {
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid URL.");
            return;
        }

        if (!isValidImageUrl(url)) {
            JOptionPane.showMessageDialog(this, "Please enter a direct image URL.");
            return;
        }

        downloadTask = threadPool.submit(() -> {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setConnectTimeout(5000); // Set timeout for connection
                int statusCode = connection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error: " + statusCode);
                }

                int fileSize = connection.getContentLength();
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                int totalBytesRead = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    totalBytesRead += bytesRead;
                    int progress = (int) ((double) totalBytesRead / fileSize * 100);
                    updateProgressBar(progress);
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();
                displayImage(outputStream.toByteArray());
                JOptionPane.showMessageDialog(this, "Image downloaded successfully.");
            } catch (IOException | InterruptedException e) {
                JOptionPane.showMessageDialog(this, "Error downloading image: " + e.getMessage());
            }
            return null;
        });
    }

    private boolean isValidImageUrl(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
    
            // Check if the response code is OK (HTTP 200)
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }
    
            // Get the Content-Type header
            String contentType = connection.getContentType();
    
            // Check if the Content-Type indicates an image format
            if (contentType != null && contentType.startsWith("image/")) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception gracefully if needed
        }
        
        return false;
    }

    private void pauseDownload() {
        if (downloadTask != null && !downloadTask.isDone() && !downloadTask.isCancelled()) {
            downloadTask.cancel(true);
            JOptionPane.showMessageDialog(this, "Download paused.");
        }
    }

    private void resumeDownload() {
        if (downloadTask != null && downloadTask.isCancelled()) {
            startDownload(urlTextField.getText().trim());
        } else {
            JOptionPane.showMessageDialog(this, "No download to resume.");
        }
    }

    private void updateProgressBar(int progress) {
        SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
    }

    private void displayImage(byte[] imageData) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            ImageIcon icon = new ImageIcon(image);
            imageLabel.setIcon(icon);
            imageListModel.addElement("Image " + (imageListModel.size() + 1));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error displaying image: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
