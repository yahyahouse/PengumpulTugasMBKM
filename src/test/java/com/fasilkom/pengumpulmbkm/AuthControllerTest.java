package com.fasilkom.pengumpulmbkm;

import com.fasilkom.pengumpulmbkm.controller.AuthController;
import com.fasilkom.pengumpulmbkm.model.JwtResponse;
import com.fasilkom.pengumpulmbkm.model.SignupRequest;
import com.fasilkom.pengumpulmbkm.model.enumeration.EProdi;
import com.fasilkom.pengumpulmbkm.model.enumeration.ERole;
import com.fasilkom.pengumpulmbkm.model.response.BaseResponse;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yahya.commonlogger.StructuredLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private AuthController authController;

    @MockitoBean(answers = Answers.RETURNS_DEEP_STUBS)
    private StructuredLogger structuredLogger;

    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    @BeforeEach
    void setUp() {
        // Deep stubs handle the chain structuredLogger.newLog().withLogLevel(...).onSuccess/onFailure(...)
    }

    @Test
    @DisplayName("test registrasi berhasil")
    void testRegisterUser_Success() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("userTest12345");
        request.setEmail("userTest@gmail.com");
        request.setNamaLengkap("User Test Lengkap");
        request.setPassword("password123");
        request.setNpm("123455689976");
        request.setRole(Collections.singleton(ERole.MAHASISWA.name()));
        request.setProdi(Collections.singleton(EProdi.TI.name()));

        MessageResponse messageResponse = new MessageResponse("User registered successfully");
        when(authService.registerUser(any(SignupRequest.class))).thenReturn(messageResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transaction.message").value("User registered successfully"));

        verify(authService, times(1)).registerUser(any(SignupRequest.class));
    }

    @Test
    @DisplayName("test registrasi username sama atau diplicate")
    void testRegisterUser_DuplicateUser() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("userTestDuplicate");
        request.setEmail("john@example.com");
        request.setNamaLengkap("John Doe Duplicate");
        request.setNpm("1234567891011");
        request.setPassword("password123");

        when(authService.registerUser(any(SignupRequest.class))).thenThrow(new IllegalArgumentException("Error: Username is already taken!"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.transaction.message").value("Error: Username is already taken!"));
    }

    @Test
    void testRegisterUser_InternalError() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("userTestError");
        request.setEmail("error@gmail.com");
        request.setNamaLengkap("Error User");
        request.setNpm("123456789012");
        request.setPassword("password123");

        when(authService.registerUser(any(SignupRequest.class))).thenThrow(new RuntimeException("Oops"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testAuthenticateUser_Success() {
        Map<String, Object> login = new HashMap<>();
        login.put("email", "user@gmail.com");
        login.put("password", "pass");

        JwtResponse expectedResponse = new JwtResponse("jwtToken", "1", "userTest", "user@gmail.com", Collections.singletonList("ROLE_MAHASISWA"));
        when(authService.authenticateUser(anyMap())).thenReturn(expectedResponse);

        ResponseEntity<BaseResponse> actualEntity = authController.authenticateUser(login);

        assertEquals(HttpStatus.OK, actualEntity.getStatusCode());
        assertEquals(expectedResponse, actualEntity.getBody().getData());
    }

    @Test
    void testAuthenticateUser_NotFound() {
        Map<String, Object> login = new HashMap<>();
        login.put("email", "unknown@gmail.com");
        when(authService.authenticateUser(anyMap())).thenThrow(new IllegalArgumentException("Not Found"));

        ResponseEntity<BaseResponse> response = authController.authenticateUser(login);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAuthenticateUser_BadCredentials() {
        Map<String, Object> login = new HashMap<>();
        login.put("email", "user@gmail.com");
        when(authService.authenticateUser(anyMap())).thenThrow(new BadCredentialsException("Wrong pass"));

        ResponseEntity<BaseResponse> response = authController.authenticateUser(login);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testAuthenticateUser_InternalError() {
        Map<String, Object> login = new HashMap<>();
        when(authService.authenticateUser(anyMap())).thenThrow(new RuntimeException("Error"));

        ResponseEntity<BaseResponse> response = authController.authenticateUser(login);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
