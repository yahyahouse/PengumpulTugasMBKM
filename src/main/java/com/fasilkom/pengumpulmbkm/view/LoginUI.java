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
import java.util.Map;

public class LoginUI extends JFrame {
    private JTextField emailTextField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginUI() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2));

        JLabel emailLabel = new JLabel("Email:");
        emailTextField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        add(emailLabel);
        add(emailTextField);
        add(passwordLabel);
        add(passwordField);
        add(new JLabel()); // Empty label for layout
        add(loginButton);
    }

    private void performLogin() {
        String email = emailTextField.getText();
        String password = new String(passwordField.getPassword());

        // Create the JSON payload
        String jsonPayload = "{" +
                "\"email\":\"" + email + "\"," +
                "\"password\":\"" + password + "\"" +
                "}";

        try {
            // Make a POST request to the API endpoint
            URL url = new URL("http://localhost:8080/api/auth/signin");
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
            JOptionPane.showMessageDialog(null, response, "Login Result", JOptionPane.INFORMATION_MESSAGE);

            // Close the connection
            conn.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred: " + ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginUI loginUI = new LoginUI();
                loginUI.setVisible(true);
            }
        });
    }
}
