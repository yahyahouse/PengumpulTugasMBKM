package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.AccountRecoveryToken;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.repository.AccountRecoveryTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AccountRecoveryServiceImpl implements AccountRecoveryService{
    private static final int TOKEN_EXPIRATION_HOURS = 24;
    private static final int TOKEN_LENGTH = 32;

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
        String resetPasswordUrl = "http://localhost:8080/account-recovery/reset-password/" + recoveryToken.getToken();
        sendResetPasswordEmail(user.getEmail(), resetPasswordUrl);
    }
    private void sendResetPasswordEmail(String email, String resetPasswordUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Password");
        message.setText("Silakan kunjungi tautan berikut untuk mereset password Anda: " + resetPasswordUrl);
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
        return LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS);
    }

    @Override
    public void saveToken(AccountRecoveryToken accountRecoveryToken) {
        recoveryTokenRepository.save(accountRecoveryToken);
    }
}
