package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersServiceImpl usersService;

    private Users mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId("user-123");
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@gmail.com");
    }

    @Test
    void testFindByUserId() {
        when(usersRepository.findByUserId("user-123")).thenReturn(mockUser);
        Users result = usersService.findByUserId("user-123");
        assertNotNull(result);
        assertEquals("user-123", result.getUserId());
    }

    @Test
    void testFindByUsername() {
        when(usersRepository.findByUsername("testuser")).thenReturn(mockUser);
        Users result = usersService.findByUsername("testuser");
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testFindByEmail() {
        when(usersRepository.findByEmail("test@gmail.com")).thenReturn(mockUser);
        Users result = usersService.findByEmail("test@gmail.com");
        assertNotNull(result);
        assertEquals("test@gmail.com", result.getEmail());
    }

    @Test
    void testUpdateUsersPassword() {
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-password");
        usersService.updateUsersPassword("new-password", "user-123");
        verify(usersRepository).updatePassword("encoded-password", "user-123");
    }

    @Test
    void testUpdateProfile() {
        usersService.updateProfile(mockUser);
        verify(usersRepository).save(mockUser);
    }

    @Test
    void testGetAllUsers() {
        List<Users> list = Arrays.asList(mockUser);
        when(usersRepository.getAllUsers()).thenReturn(list);
        List<Users> result = usersService.getAllUsers();
        assertEquals(1, result.size());
    }

    @Test
    void testSavePassword() {
        usersService.savePassword(mockUser);
        verify(usersRepository).save(mockUser);
    }
}
