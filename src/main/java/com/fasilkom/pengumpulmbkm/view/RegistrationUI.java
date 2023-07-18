package com.fasilkom.pengumpulmbkm.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegistrationUI extends JFrame {
    private JTextField usernameTextField;
    private JTextField emailTextField;
    private JTextField namaLengkapTextField;
    private JPasswordField passwordField;
    private JCheckBox dosenCheckBox;
    private JCheckBox mahasiswaCheckBox;
    private JCheckBox adminCheckBox;
    private JCheckBox tiCheckBox;
    private JCheckBox programBangkitCheckBox;
    private JButton registerButton;

    public RegistrationUI() {
        setTitle("Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameTextField = new JTextField();

        JLabel emailLabel = new JLabel("Email:");
        emailTextField = new JTextField();

        JLabel namaLengkapLabel = new JLabel("Nama Lengkap:");
        namaLengkapTextField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JLabel roleLabel = new JLabel("Role:");
        dosenCheckBox = new JCheckBox("Dosen");
        mahasiswaCheckBox = new JCheckBox("Mahasiswa");
        adminCheckBox = new JCheckBox("Admin");

        JLabel prodiLabel = new JLabel("Prodi:");
        tiCheckBox = new JCheckBox("TI");

        JLabel programLabel = new JLabel("Program:");
        programBangkitCheckBox = new JCheckBox("Bangkit");

        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });

        add(usernameLabel);
        add(usernameTextField);
        add(emailLabel);
        add(emailTextField);
        add(namaLengkapLabel);
        add(namaLengkapTextField);
        add(passwordLabel);
        add(passwordField);
        add(roleLabel);
        add(dosenCheckBox);
        add(new JLabel()); // Empty label for layout
        add(mahasiswaCheckBox);
        add(new JLabel()); // Empty label for layout
        add(adminCheckBox);
        add(prodiLabel);
        add(tiCheckBox);
        add(programLabel);
        add(programBangkitCheckBox);
        add(new JLabel()); // Empty label for layout
        add(registerButton);
    }

    private void performRegistration() {
        String username = usernameTextField.getText();
        String email = emailTextField.getText();
        String namaLengkap = namaLengkapTextField.getText();
        String password = new String(passwordField.getPassword());

        // Create the JSON payload
        String jsonPayload = "{" +
                "\"username\":\"" + username + "\"," +
                "\"email\":\"" + email + "\"," +
                "\"namaLengkap\":\"" + namaLengkap + "\"," +
                "\"password\":\"" + password + "\"," +
                "\"role\":[";

        if (dosenCheckBox.isSelected()) {
            jsonPayload += "\"DOSEN\",";
        }
        if (mahasiswaCheckBox.isSelected()) {
            jsonPayload += "\"MAHASISWA\",";
        }
        if (adminCheckBox.isSelected()) {
            jsonPayload += "\"ADMIN\",";
        }
        // Remove the trailing comma if any
        if (jsonPayload.endsWith(",")) {
            jsonPayload = jsonPayload.substring(0, jsonPayload.length() - 1);
        }

        jsonPayload += "]," +
                "\"prodi\":[";

        if (tiCheckBox.isSelected()) {
            jsonPayload += "\"TI\",";
        }
        // Remove the trailing comma if any
        if (jsonPayload.endsWith(",")) {
            jsonPayload = jsonPayload.substring(0, jsonPayload.length() - 1);
        }

        jsonPayload += "]," +
                "\"program\":[";

        if (programBangkitCheckBox.isSelected()) {
            jsonPayload += "\"BANGKIT\",";
        }
        // Remove the trailing comma if any
        if (jsonPayload.endsWith(",")) {
            jsonPayload = jsonPayload.substring(0, jsonPayload.length() - 1);
        }

        jsonPayload += "]" +
                "}";

        try {
            // Make a POST request to the API endpoint
            URL url = new URL("http://localhost:8080/api/auth/signup");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            conn.getOutputStream().write(jsonPayload.getBytes());

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            // Display the response
            String response = responseBuilder.toString();
            JOptionPane.showMessageDialog(null, response, "Registration Result", JOptionPane.INFORMATION_MESSAGE);

            // Close the connection
            conn.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred: " + ex.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                RegistrationUI registrationUI = new RegistrationUI();
                registrationUI.setVisible(true);
            }
        });
    }
}
