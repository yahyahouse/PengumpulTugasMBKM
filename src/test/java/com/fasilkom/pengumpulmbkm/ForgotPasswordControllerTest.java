package com.fasilkom.pengumpulmbkm;

import com.fasilkom.pengumpulmbkm.controller.ForgotPasswordController;
import com.fasilkom.pengumpulmbkm.model.AccountRecoveryToken;
import com.fasilkom.pengumpulmbkm.model.response.BaseResponse;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.AccountRecoveryService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import com.fasilkom.pengumpulmbkm.util.CommonConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class ForgotPasswordControllerTest {

    @Autowired
    private ForgotPasswordController forgotPasswordController;

    @MockitoBean
    private UsersService usersService;

    @MockitoBean
    private AccountRecoveryService recoveryService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private Users mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setEmail("test@gmail.com");
        mockUser.setUserId("user-123");
    }

    @Test
    void testCreateRecoveryToken_Success() {
        Model model = Mockito.mock(Model.class);
        when(usersService.findByEmail("test@gmail.com")).thenReturn(mockUser);

        ResponseEntity<BaseResponse> response = forgotPasswordController.createRecoveryToken("test@gmail.com", model);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(CommonConstant.RECOVERY_SUCCESS, response.getBody().getTransaction().getMessage());
        verify(recoveryService).createRecoveryToken(mockUser);
        verify(model).addAttribute(eq("message"), anyString());
    }

    @Test
    void testCreateRecoveryToken_UserNotFound() {
        Model model = Mockito.mock(Model.class);
        when(usersService.findByEmail("unknown@gmail.com")).thenReturn(null);

        ResponseEntity<BaseResponse> response = forgotPasswordController.createRecoveryToken("unknown@gmail.com", model);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(CommonConstant.EMAIL_NOT_FOUND, response.getBody().getTransaction().getMessage());
    }

    @Test
    void testResetPassword_Success() {
        AccountRecoveryToken token = new AccountRecoveryToken();
        token.setUser(mockUser);
        
        when(recoveryService.getRecoveryTokenByToken("token-123")).thenReturn(token);
        when(recoveryService.validateRecoveryToken("token-123")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-password");

        ResponseEntity<BaseResponse> response = forgotPasswordController.resetPassword("token-123", "new-password", "new-password");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset successful", response.getBody().getTransaction().getMessage());
        verify(usersService).savePassword(mockUser);
        verify(recoveryService).saveToken(token);
    }

    @Test
    void testResetPassword_InvalidToken() {
        when(recoveryService.getRecoveryTokenByToken("invalid")).thenReturn(null);

        ResponseEntity<BaseResponse> response = forgotPasswordController.resetPassword("invalid", "pass", "pass");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid token", response.getBody().getTransaction().getMessage());
    }

    @Test
    void testResetPassword_ExpiredToken() {
        AccountRecoveryToken token = new AccountRecoveryToken();
        when(recoveryService.getRecoveryTokenByToken("expired")).thenReturn(token);
        when(recoveryService.validateRecoveryToken("expired")).thenReturn(true);

        ResponseEntity<BaseResponse> response = forgotPasswordController.resetPassword("expired", "pass", "pass");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Token Expired", response.getBody().getTransaction().getMessage());
    }

    @Test
    void testResetPassword_PasswordMismatch() {
        AccountRecoveryToken token = new AccountRecoveryToken();
        when(recoveryService.getRecoveryTokenByToken("valid")).thenReturn(token);
        when(recoveryService.validateRecoveryToken("valid")).thenReturn(false);

        ResponseEntity<BaseResponse> response = forgotPasswordController.resetPassword("valid", "pass1", "pass2");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(CommonConstant.PASSWORD_SAMA, response.getBody().getTransaction().getMessage());
    }
}
