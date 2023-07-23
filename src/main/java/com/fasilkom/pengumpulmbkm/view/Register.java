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

public class Register extends JDialog{
    private JTextField usernameTextField;
    private JTextField emailTextField;
    private JTextField namaLengkapTextField;
    private JCheckBox ADMIN;
    private JCheckBox DOSEN;
    private JCheckBox MAHASISWA;
    private JRadioButton TI;
    private JRadioButton SI;
    private JComboBox comboBox1;
    private JPasswordField passwordRegisterField;
    private JButton registerButton;
    private JTextField npmTextField;
    private JPanel registerPanel;
    private ButtonGroup Prodi;
    private ButtonGroup Role;

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                Register register = new Register(null);
//                register.setVisible(true);
//            }
//        });
//    }

    public Register(JFrame parent) {
        super(parent);
        setTitle("Login");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450,450));
        setMaximumSize(new Dimension(450,450));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
                dispose();
            }
        });
    }

    private void performRegistration() {
        String username = usernameTextField.getText();
        String email = emailTextField.getText();
        String namaLengkap = namaLengkapTextField.getText();
        String password = new String(passwordRegisterField.getPassword());
        String npm = npmTextField.getText();

        // Create the JSON payload
        String jsonPayload = "{" +
                "\"username\":\"" + username + "\"," +
                "\"email\":\"" + email + "\"," +
                "\"namaLengkap\":\"" + namaLengkap + "\"," +
                "\"password\":\"" + password + "\"," +
                "\"npm\":" + npm +"," +
                "\"role\":[";

        if (DOSEN.isSelected()) {
            jsonPayload += "\"DOSEN\",";
        }
        if (MAHASISWA.isSelected()) {
            jsonPayload += "\"MAHASISWA\",";
        }
        if (ADMIN.isSelected()) {
            jsonPayload += "\"ADMIN\",";
        }
        // Remove the trailing comma if any
        if (jsonPayload.endsWith(",")) {
            jsonPayload = jsonPayload.substring(0, jsonPayload.length() - 1);
        }

        jsonPayload += "]," +
                "\"prodi\":[";

        if (TI.isSelected()) {
            jsonPayload += "\"TI\",";
        }
        if (SI.isSelected()) {
            jsonPayload += "\"SI\",";
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
}
