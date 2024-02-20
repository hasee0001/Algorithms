package raven.login;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import raven.component.PostPanel;
import raven.component.SearchPanel;
import raven.component.UserPanel;

import java.awt.*;

public class Home extends JFrame {
    public Home(String loggedInEmail) {
        setLayout(new MigLayout("fill, insets 20", "[fill][fill][fill]", "[fill][fill][fill]"));

        JPanel userPanel = new UserPanel(loggedInEmail);
        add(userPanel, "cell 0 0, width 200px, height 300px");

        JPanel postPanel = new PostPanel(loggedInEmail);
        add(postPanel, "cell 1 0, width 600px");

        JPanel searchPanel = new SearchPanel();
        add(searchPanel, "cell 0 1 2 1");

        // Other initialization...
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Other methods...
}
