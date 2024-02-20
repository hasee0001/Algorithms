package raven.main;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import raven.login.Login;
import raven.manager.FormsManager;
import raven.toast.Notifications;

import javax.swing.*;
import java.awt.*;

public class Application extends JFrame {

    private static Application app;

    public Application() {
        init();
    }

    private void init() {
        setTitle("Lets talk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
        setContentPane(new Login());
        Notifications.getInstance().setJFrame(this);
        FormsManager.getInstance().initApplication(this);
    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("raven.themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(() -> new Application().setVisible(true));
    }
}
