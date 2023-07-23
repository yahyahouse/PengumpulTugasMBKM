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
public class AccountRecoveryServiceImpl implements AccountRecoveryService {
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
        String resetPasswordUrl = recoveryToken.getToken();
        sendResetPasswordEmail(user.getEmail(), resetPasswordUrl);
    }

    private void sendResetPasswordEmail(String email, String resetPasswordUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Password");
        message.setText("We have received a request to reset the password for your account. To proceed with the password reset, please click on the link below:\n" +
                "\n" +
                "Reset Password: \n" + resetPasswordUrl+
                "\n" +
                "Please note that this link will expire in [Insert Expiry Time, e.g., 24 hours].\n" +
                "\n" +
                "If you did not request this password reset or believe this is an unauthorized attempt, please ignore this email. Your account will remain secure.  " );
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
