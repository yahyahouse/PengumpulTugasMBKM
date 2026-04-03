package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.AccountRecoveryToken;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.repository.AccountRecoveryTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountRecoveryServiceTest {

    @Mock
    private AccountRecoveryTokenRepository recoveryTokenRepository;

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private AccountRecoveryServiceImpl accountRecoveryService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(accountRecoveryService, "tokenExpirationHours", 24);
    }

    @Test
    void testCreateRecoveryToken() {
        Users user = new Users();
        user.setEmail("test@gmail.com");

        accountRecoveryService.createRecoveryToken(user);

        verify(recoveryTokenRepository, times(1)).save(any(AccountRecoveryToken.class));
        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testGetRecoveryTokenByToken() {
        AccountRecoveryToken token = new AccountRecoveryToken();
        when(recoveryTokenRepository.findByToken("token-123")).thenReturn(token);

        AccountRecoveryToken result = accountRecoveryService.getRecoveryTokenByToken("token-123");
        assertNotNull(result);
    }

    @Test
    void testGenerateToken() {
        String token = accountRecoveryService.generateToken();
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testCalculateExpirationDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = accountRecoveryService.calculateExpirationDate();
        assertTrue(expiration.isAfter(now));
    }

    @Test
    void testSaveToken() {
        AccountRecoveryToken token = new AccountRecoveryToken();
        accountRecoveryService.saveToken(token);
        verify(recoveryTokenRepository).save(token);
    }

    @Test
    void testValidateRecoveryToken_Invalid() {
        // Token null
        when(recoveryTokenRepository.findByToken("invalid")).thenReturn(null);
        assertTrue(accountRecoveryService.validateRecoveryToken("invalid"));

        // Token expired
        AccountRecoveryToken token = new AccountRecoveryToken();
        token.setExpirationDate(LocalDateTime.now().minusHours(1));
        when(recoveryTokenRepository.findByToken("expired")).thenReturn(token);
        assertTrue(accountRecoveryService.validateRecoveryToken("expired"));
    }

    @Test
    void testValidateRecoveryToken_Valid() {
        AccountRecoveryToken token = new AccountRecoveryToken();
        token.setExpirationDate(LocalDateTime.now().plusHours(1));
        when(recoveryTokenRepository.findByToken("valid")).thenReturn(token);
        assertFalse(accountRecoveryService.validateRecoveryToken("valid"));
    }
}
