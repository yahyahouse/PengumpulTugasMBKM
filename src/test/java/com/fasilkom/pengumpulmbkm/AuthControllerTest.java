package com.fasilkom.pengumpulmbkm;

import com.fasilkom.pengumpulmbkm.config.JwtUtils;
import com.fasilkom.pengumpulmbkm.controller.AuthController;
import com.fasilkom.pengumpulmbkm.model.JwtResponse;
import com.fasilkom.pengumpulmbkm.model.SignupRequest;
import com.fasilkom.pengumpulmbkm.model.UserDetailsImpl;
import com.fasilkom.pengumpulmbkm.model.enumeration.EProdi;
import com.fasilkom.pengumpulmbkm.model.enumeration.EProgram;
import com.fasilkom.pengumpulmbkm.model.enumeration.ERole;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.repository.UsersRepository;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.stream.Collectors;

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

    @MockBean
    private UsersRepository usersRepository;
    @MockBean
    private UsersService usersService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;

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
        request.setNamaLengkap("userTestLengkap");
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
//    @DisplayName("test login berhasil")
//    public void testLogin_Success() throws Exception {
//        Map<String, Object> login = new HashMap<>();
//        login.put("email", "userTest@gmail.com");
//        login.put("password", "userTest");
//
//        Users users = new Users();
//        users.setEmail("userTest@gmail.com");
//        users.setUsername("userTest");
//        users.setPassword("userTest");
//
//        when(usersRepository.findUsersByEmail(login.get("email").toString())).thenReturn(users);
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(users.getUsername(), login.get("password"));
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
//
//        String jwt = "dummy-token";
//        when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwt);
//
//        UserDetailsImpl userDetails = new UserDetailsImpl(users);
//        when(userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList()));
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//
//
//        mockMvc.perform(post("/signin")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(login)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value(jwt));
//    }
}

