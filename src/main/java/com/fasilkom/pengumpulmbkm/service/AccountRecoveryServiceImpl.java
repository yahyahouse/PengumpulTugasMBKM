package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.AccountRecoveryToken;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.repository.AccountRecoveryTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AccountRecoveryServiceImpl implements AccountRecoveryService {

    @Value("${tokenExpiredHours}")
    private int tokenExpirationHours;

    @Autowired
    private AccountRecoveryTokenRepository recoveryTokenRepository;
    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void createRecoveryToken(Users user) {
        AccountRecoveryToken recoveryToken = new AccountRecoveryToken();
        recoveryToken.setUser(user);
        recoveryToken.setEmail(user.getEmail());
        recoveryToken.setToken(generateToken());
        recoveryToken.setExpirationDate(calculateExpirationDate());
        recoveryTokenRepository.save(recoveryToken);
        String resetPasswordUrl = recoveryToken.getToken();
        sendResetPasswordEmail(user.getEmail(), resetPasswordUrl);
    }

    private void sendResetPasswordEmail(String email, String resetPasswordUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Password");
        message.setText("Terima kasih telah menggunakan layanan kami! Untuk melanjutkan proses verifikasi akun Anda, silakan gunakan token berikut:\n" +
                "\n" +
                "Token: "+resetPasswordUrl+"\n" +
                "\n" +
                "Harap jangan bagikan token ini dengan siapa pun kecuali dengan tim dukungan kami. Token ini adalah kunci untuk mengamankan akun Anda, dan kami selalu menjaga keamanan informasi Anda dengan sepenuh hati.\n" +
                "\n" +
                "Jika Anda tidak merasa melakukan tindakan ini, mohon abaikan email ini. Akun Anda tetap aman dan tidak akan terpengaruh.");
        emailSender.send(message);
    }

    @Override
    public AccountRecoveryToken getRecoveryTokenByToken(String token) {
        return recoveryTokenRepository.findByToken(token);
    }

    @Override
    public String generateToken() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    @Override
    public LocalDateTime calculateExpirationDate() {
        return LocalDateTime.now().plusHours(tokenExpirationHours);
    }

    @Override
    public void saveToken(AccountRecoveryToken accountRecoveryToken) {
        recoveryTokenRepository.save(accountRecoveryToken);
    }

    @Override
    public boolean validateRecoveryToken(String token) {
        AccountRecoveryToken recoveryToken = getRecoveryTokenByToken(token);
        return recoveryToken == null || !recoveryToken.getExpirationDate().isAfter(LocalDateTime.now());
    }

}
