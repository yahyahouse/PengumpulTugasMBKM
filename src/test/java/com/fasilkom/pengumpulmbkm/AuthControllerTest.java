package com.fasilkom.pengumpulmbkm;

import com.fasilkom.pengumpulmbkm.config.JwtUtils;
import com.fasilkom.pengumpulmbkm.config.enumConfig.EnumConfig;
import com.fasilkom.pengumpulmbkm.controller.AuthController;
import com.fasilkom.pengumpulmbkm.model.JwtResponse;
import com.fasilkom.pengumpulmbkm.model.SignupRequest;
import com.fasilkom.pengumpulmbkm.model.UserDetailsImpl;
import com.fasilkom.pengumpulmbkm.model.enumeration.EProdi;
import com.fasilkom.pengumpulmbkm.model.enumeration.EProgram;
import com.fasilkom.pengumpulmbkm.model.enumeration.ERole;
import com.fasilkom.pengumpulmbkm.model.roles.Roles;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.repository.RoleRepository;
import com.fasilkom.pengumpulmbkm.repository.UsersRepository;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private EnumConfig enumConfig;

    @MockBean
    private UsersRepository usersRepository;
    @MockBean
    private UsersService usersService;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    JwtUtils jwtUtils;
    @MockBean
    private RoleRepository roleRepository;
    @InjectMocks
    private AuthController authController;
    private static final Logger LOG = LoggerFactory.getLogger(EnumConfig.class);

    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }


    @Test
    @DisplayName("test registrasi berhasil")
    public void testRegisterUser_Success() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("userTest");
        request.setEmail("userTest@gmail.com");
        request.setNamaLengkap("userTest");
        request.setPassword("userTest");
        request.setRole(Collections.singleton(ERole.MAHASISWA.name()));
        request.setProdi(Collections.singleton(EProdi.TI.name()));
        request.setProgram(Collections.singleton(EProgram.BANGKIT.name()));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"message\":\"User registered successfully\"}"));

        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    @DisplayName("test registrasi username sama atau diplicate")
    public void testRegisterUser_DuplicateUser() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("john_doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");

        // Simulasikan situasi user dengan username yang sama sudah terdaftar
        when(usersRepository.existsByUsername(request.getUsername())).thenReturn(true);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"Error: Username is already taken!\"}"));

        verify(usersRepository, times(0)).save(any(Users.class));
    }

//    @Test
//    public void testAuthenticateUser() {
//        // Prepare test data
//        String email = "userTest@gmail.com";
//        String password = "userTest";
//        Map<String, Object> login = new HashMap<>();
//        login.put("email", email);
//        login.put("password", password);
//
//        Users user = new Users();
//        user.setUserId(1);
//        user.setUsername("userTest");
//        user.setEmail(email);
//        user.setPassword(password);
//
//        Roles role = new Roles();
//        role.setRoleId(1);
//        role.setName(ERole.MAHASISWA);
//        user.setRoles(Collections.singleton(role));
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String jwt = "jwtToken";
//
//        UserDetailsImpl userDetails = new UserDetailsImpl(user);
//        List<String> roles = userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList());
//
//        JwtResponse expectedResponse = new JwtResponse(jwt, userDetails.getUserId(), userDetails.getUsername(),
//                userDetails.getEmail(), roles);
//        ResponseEntity<JwtResponse> expectedEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
//
//        // Mock the dependencies
//        when(usersRepository.findUsersByEmail(email)).thenReturn(user);
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
//        when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwt);
//
//        // Call the method under test
//        ResponseEntity<JwtResponse> actualEntity = authController.authenticateUser(login);
//
//        // Verify the result
//        assertEquals(expectedEntity.getStatusCode(), actualEntity.getStatusCode());
//        assertEquals(expectedEntity.getBody(), actualEntity.getBody());
//
//        // Verify the mock invocations
//        verify(usersRepository, times(1)).findUsersByEmail(email);
//        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verify(jwtUtils, times(1)).generateJwtToken(authentication);
//    }

}

