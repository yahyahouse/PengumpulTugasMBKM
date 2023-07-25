package com.fasilkom.pengumpulmbkm.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class LupaPassword extends JDialog{
    private JTextField emailTextField;
    private JButton kirimButton;
    private JPanel lupaPasswordPanel;
    private JButton inputTokenButton;

    public LupaPassword(JFrame parent) {
        super(parent);
        setTitle("Lupa Password");
        setContentPane(lupaPasswordPanel);
        setMinimumSize(new Dimension(450,450));
        setMaximumSize(new Dimension(450,450));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        kirimButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailTextField.getText();
                sendResetPasswordRequest(email);
                dispose();
                ResetPassword resetPassword = new ResetPassword(null);
                resetPassword.setVisible(true);
            }
        });
        inputTokenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResetPassword resetPassword = new ResetPassword(null);
                resetPassword.setVisible(true);
            }
        });
    }

    private void sendResetPasswordRequest(String email) {
        try {
            String url = "http://localhost:8080/account-recovery/create-token";
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            // Encode form data
            String postData = "email=" + URLEncoder.encode(email, "UTF-8");

            // Send POST request
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(postData);
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
                showMessage("Gagal mengirim permintaan reset password");
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
