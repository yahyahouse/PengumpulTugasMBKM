package com.fasilkom.pengumpulmbkm.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ResetPassword extends JDialog {
    private JTextField KodeField;
    private JTextField newPasswordField;
    private JTextField RetypePasswordField;
    private JButton kirimButton;
    private JPanel resetPassword;

    public ResetPassword(JFrame parent) {
        super(parent);
        setTitle("Reset Password");
        setContentPane(resetPassword);
        setMinimumSize(new Dimension(450, 450));
        setMaximumSize(new Dimension(450, 450));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        kirimButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String token = KodeField.getText();
                String newPassword = newPasswordField.getText();
                String retypePassword = RetypePasswordField.getText();
                sendResetPasswordRequest(token, newPassword, retypePassword);
                dispose();
            }
        });
    }

    private void sendResetPasswordRequest(String token, String newPassword, String retypePassword) {
        try {
            String url = "http://localhost:8080/account-recovery/reset-password/" + token;
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            // Encode form data
            String formData = "password=" + URLEncoder.encode(newPassword, "UTF-8") +
                    "&passwordRetype=" + URLEncoder.encode(retypePassword, "UTF-8");

            // Send POST request
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(formData);
            wr.flush();
            wr.close();

            // Get response
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Berhasil, baca respons dari server
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = in.readLine();
                in.close();

                // Tampilkan pesan respons dari server
                showMessage(response);
            } else {
                // Gagal, tampilkan pesan error
                showMessage("Token Salah");
            }

            conn.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

}
