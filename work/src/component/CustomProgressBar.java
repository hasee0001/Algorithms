package component;
import javax.swing.*;
import java.awt.*;

public class CustomProgressBar extends JProgressBar {

    public CustomProgressBar() {
        super();
        setStringPainted(true);
        setString("");
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        double percent = (double) getValue() / (double) getMaximum();
        Rectangle bounds = getBounds();
        Insets insets = getInsets();
        int barWidth = (int) ((bounds.width - insets.left - insets.right) * percent);
        int barHeight = bounds.height - insets.top - insets.bottom;

        if (percent <= 0.3) {
            g2d.setColor(Color.RED);
        } else if (percent <= 0.7) {
            g2d.setColor(Color.YELLOW);
        } else {
            g2d.setColor(Color.GREEN);
        }

        g2d.fillRect(insets.left, insets.top, barWidth, barHeight);

        // Draw percentage text
        String text = String.format("%d%%", (int) (percent * 100));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        int x = (bounds.width - textWidth) / 2;
        int y = (bounds.height - textHeight) / 2 + fm.getAscent();
        g2d.setColor(getForeground());
        g2d.drawString(text, x, y);

        g2d.dispose();
    }
}
