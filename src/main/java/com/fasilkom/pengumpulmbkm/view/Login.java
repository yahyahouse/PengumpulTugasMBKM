package com.fasilkom.pengumpulmbkm.view;

import org.json.JSONObject;

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

public class Login extends JDialog{
    private JTextField emailTextField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registrasiButton;
    private JPanel loginPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Login login = new Login(null);
                login.setVisible(true);
            }
        });
    }
    public Login(JFrame parent) {
        super(parent);
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450,450));
        setMaximumSize(new Dimension(450,450));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailTextField.getText();
                String password = String.valueOf(passwordField.getPassword());
                performLogin(email,password);
                try {
                    LaporanAdmin laporanAdmin = new LaporanAdmin(null);
                    laporanAdmin.setVisible(true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        registrasiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Register register = new Register(null);
                register.setVisible(true);
            }
        });
    }

    private void performLogin(String email, String password) {
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
            JOptionPane.showMessageDialog(null, "Login berhasil", "Login Result", JOptionPane.INFORMATION_MESSAGE);
            JSONObject jsonResponse = new JSONObject(response);
            String token = jsonResponse.getString("token");
            UserSession.setToken(token);

            // Close the connection
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred: " + ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

}