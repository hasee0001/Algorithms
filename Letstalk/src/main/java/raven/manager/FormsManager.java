package raven.manager;

import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

import raven.login.Home;
import raven.login.Login;
import raven.login.Register;
import raven.login.home1;
import raven.main.Application;

import javax.swing.*;
import java.awt.*;

public class FormsManager {
    private Application application;
    private static FormsManager instance;

    public static FormsManager getInstance() {
        if (instance == null) {
            instance = new FormsManager();
        }
        return instance;
    }

    private FormsManager() {

    }

    public void initApplication(Application application) {
        this.application = application;
    }

    public void showForm(Register register) {
        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            application.setContentPane(register);
            application.revalidate();
            application.repaint();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }

    // Additional method to show the Home form
    public void showHomeForm() {
        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            Home homeForm = new Home("Haseena"); 
            application.setContentPane(homeForm);
            application.revalidate();
            application.repaint();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }

    public void showForm(Home home) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showForm'");
    }

    public void showForm(Login login) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showForm'");
    }

}
