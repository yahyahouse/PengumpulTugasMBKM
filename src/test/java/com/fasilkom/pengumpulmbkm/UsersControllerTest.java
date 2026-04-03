package com.fasilkom.pengumpulmbkm;

import com.fasilkom.pengumpulmbkm.controller.UsersController;
import com.fasilkom.pengumpulmbkm.model.response.BaseResponse;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import com.fasilkom.pengumpulmbkm.util.CommonConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class UsersControllerTest {

    @Autowired
    private UsersController usersController;

    @MockitoBean
    private UsersService usersService;

    @MockitoBean
    private DosenService dosenService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private Users mockUser;
    private Dosen mockDosen;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId("user-123");
        mockUser.setUsername("testuser");
        mockUser.setPassword("encoded-password");

        mockDosen = new Dosen();
        mockDosen.setDosenId("dosen-123");
        mockDosen.setUserId(mockUser);

        auth = new UsernamePasswordAuthenticationToken("testuser", "password");
    }

    @Test
    void testGetDetailUser() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);

        ResponseEntity<BaseResponse> response = usersController.getDetailUser(auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateUsersPassword_Success() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(usersService.findByUserId(anyString())).thenReturn(mockUser);
        when(passwordEncoder.matches("old-pass", "encoded-password")).thenReturn(true);

        ResponseEntity<BaseResponse> response = usersController.updateUsersPassword(
                "old-pass", "new-pass", "new-pass", auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(usersService).updateUsersPassword("new-pass", "user-123");
    }

    @Test
    void testUpdateUsersPassword_WrongOldPassword() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(usersService.findByUserId(anyString())).thenReturn(mockUser);
        when(passwordEncoder.matches("wrong-pass", "encoded-password")).thenReturn(false);

        ResponseEntity<BaseResponse> response = usersController.updateUsersPassword(
                "wrong-pass", "new-pass", "new-pass", auth);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(CommonConstant.SALAH_PASSWORD, response.getBody().getTransaction().getMessage());
    }

    @Test
    void testUpdateUsersProfile_Success() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(usersService.findByUserId(anyString())).thenReturn(mockUser);
        when(passwordEncoder.matches("password", "encoded-password")).thenReturn(true);

        ResponseEntity<BaseResponse> response = usersController.updateUsersProfile(
                "08123456789", "New Name", "123456789012", "password", auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(usersService).updateProfile(mockUser);
    }

    @Test
    void testGetAllDosen() {
        when(dosenService.getAllDosen()).thenReturn(Collections.singletonList(mockDosen));

        ResponseEntity<BaseResponse> response = usersController.getAllDosen();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> data = (List<?>) response.getBody().getData();
        assertEquals(1, data.size());
    }

    @Test
    void testGetDetailDosenByDosenId_Success() {
        when(dosenService.getDosenByDosenId("dosen-123")).thenReturn(mockDosen);

        ResponseEntity<BaseResponse> response = usersController.getDetailDosenByDosenId("dosen-123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetDetailDosenByDosenId_NotFound() {
        when(dosenService.getDosenByDosenId("unknown")).thenReturn(null);

        ResponseEntity<BaseResponse> response = usersController.getDetailDosenByDosenId("unknown");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
