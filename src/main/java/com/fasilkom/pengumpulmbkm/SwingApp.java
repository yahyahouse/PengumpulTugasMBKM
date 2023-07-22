package com.fasilkom.pengumpulmbkm;

import com.fasilkom.pengumpulmbkm.view.Login;

import javax.swing.*;

public class SwingApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Login login = new Login(null);
            login.setVisible(true);
        });
    }
}
