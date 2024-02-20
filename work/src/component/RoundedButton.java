package component;
import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {

    public RoundedButton(String text) {
        super(text);
        init();
    }

    public RoundedButton(Icon icon) {
        super(icon);
        init();
    }

    private void init() {
        setContentAreaFilled(false); // Make the button transparent
        setBorderPainted(false); // Remove the border
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.gray); // Change color when button is pressed
        } else {
            g.setColor(getBackground());
        }
        // Draw rounded rectangle
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Do not paint border
    }
}
