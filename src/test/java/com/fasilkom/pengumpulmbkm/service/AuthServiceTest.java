package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.config.JwtUtils;
import com.fasilkom.pengumpulmbkm.model.JwtResponse;
import com.fasilkom.pengumpulmbkm.model.SignupRequest;
import com.fasilkom.pengumpulmbkm.model.UserDetailsImpl;
import com.fasilkom.pengumpulmbkm.model.enumeration.EProdi;
import com.fasilkom.pengumpulmbkm.model.enumeration.ERole;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.model.roles.Prodi;
import com.fasilkom.pengumpulmbkm.model.roles.Roles;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.repository.ProdiRepository;
import com.fasilkom.pengumpulmbkm.repository.ProgramRepository;
import com.fasilkom.pengumpulmbkm.repository.RoleRepository;
import com.fasilkom.pengumpulmbkm.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ProdiRepository prodiRepository;
    @Mock
    private ProgramRepository programRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    private Users mockUser;
    private Roles mockRole;
    private Prodi mockProdi;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@gmail.com");
        mockUser.setPassword("password");
        mockUser.setUserId("user-123");

        mockRole = new Roles();
        mockRole.setName(ERole.MAHASISWA);
        mockUser.setRoles(Collections.singleton(mockRole));

        mockProdi = new Prodi();
        mockProdi.setName(EProdi.TI);
        mockUser.setProgramStudi(Collections.singleton(mockProdi));
    }

    @Test
    void testAuthenticateUser_Success() {
        Map<String, Object> login = new HashMap<>();
        login.put("email", "test@gmail.com");
        login.put("password", "password");

        when(usersRepository.findUsersByEmail("test@gmail.com")).thenReturn(mockUser);
        
        Authentication auth = mock(Authentication.class);
        UserDetailsImpl userDetails = UserDetailsImpl.build(mockUser);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtUtils.generateJwtToken(auth)).thenReturn("jwt-token");

        JwtResponse result = authService.authenticateUser(login);

        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testAuthenticateUser_NotFound() {
        Map<String, Object> login = new HashMap<>();
        login.put("email", "unknown@gmail.com");
        when(usersRepository.findUsersByEmail("unknown@gmail.com")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> authService.authenticateUser(login));
    }

    @Test
    void testRegisterUser_SuccessWithAllRoles() {
        SignupRequest request = new SignupRequest();
        request.setUsername("newuser");
        request.setEmail("new@gmail.com");
        request.setPassword("password");
        request.setNamaLengkap("New User");
        request.setNpm("1234567890");
        request.setRole(new HashSet<>(Arrays.asList("ADMIN", "DOSEN", "MAHASISWA")));
        request.setProdi(new HashSet<>(Arrays.asList("TI", "SI")));

        when(usersRepository.existsByUsername("newuser")).thenReturn(false);
        when(usersRepository.existsByEmail("new@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        
        when(roleRepository.findByName(ERole.ADMIN)).thenReturn(Optional.of(new Roles()));
        when(roleRepository.findByName(ERole.DOSEN)).thenReturn(Optional.of(new Roles()));
        when(roleRepository.findByName(ERole.MAHASISWA)).thenReturn(Optional.of(new Roles()));
        
        when(prodiRepository.findByName(EProdi.TI)).thenReturn(Optional.of(new Prodi()));
        when(prodiRepository.findByName(EProdi.SI)).thenReturn(Optional.of(new Prodi()));

        MessageResponse result = authService.registerUser(request);

        assertEquals("User registered successfully", result.getMessage());
        verify(usersRepository).save(any(Users.class));
    }

    @Test
    void testRegisterUser_DefaultRoleAndProdi() {
        SignupRequest request = new SignupRequest();
        request.setUsername("defaultuser");
        request.setEmail("default@gmail.com");
        request.setPassword("password");
        request.setRole(null); // Should default to MAHASISWA
        request.setProdi(null); // Should default to TI

        when(usersRepository.existsByUsername("defaultuser")).thenReturn(false);
        when(usersRepository.existsByEmail("default@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        
        when(roleRepository.findByName(ERole.MAHASISWA)).thenReturn(Optional.of(mockRole));
        when(prodiRepository.findByName(EProdi.TI)).thenReturn(Optional.of(mockProdi));

        MessageResponse result = authService.registerUser(request);

        assertEquals("User registered successfully", result.getMessage());
        verify(usersRepository).save(any(Users.class));
    }

    @Test
    void testRegisterUser_RoleNotFound() {
        SignupRequest request = new SignupRequest();
        request.setUsername("user");
        request.setEmail("user@gmail.com");
        request.setRole(Collections.singleton("MAHASISWA"));

        when(usersRepository.existsByUsername("user")).thenReturn(false);
        when(usersRepository.existsByEmail("user@gmail.com")).thenReturn(false);
        when(roleRepository.findByName(ERole.MAHASISWA)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.registerUser(request));
    }

    @Test
    void testRegisterUser_ProdiNotFound() {
        SignupRequest request = new SignupRequest();
        request.setUsername("user");
        request.setEmail("user@gmail.com");
        request.setProdi(Collections.singleton("TI"));

        when(usersRepository.existsByUsername("user")).thenReturn(false);
        when(usersRepository.existsByEmail("user@gmail.com")).thenReturn(false);
        when(roleRepository.findByName(ERole.MAHASISWA)).thenReturn(Optional.of(mockRole));
        when(prodiRepository.findByName(EProdi.TI)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.registerUser(request));
    }

    @Test
    void testRegisterUser_UsernameTaken() {
        SignupRequest request = new SignupRequest();
        request.setUsername("taken");
        when(usersRepository.existsByUsername("taken")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(request));
    }

    @Test
    void testRegisterUser_EmailTaken() {
        SignupRequest request = new SignupRequest();
        request.setUsername("user");
        request.setEmail("taken@gmail.com");
        when(usersRepository.existsByUsername("user")).thenReturn(false);
        when(usersRepository.existsByEmail("taken@gmail.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(request));
    }
}
