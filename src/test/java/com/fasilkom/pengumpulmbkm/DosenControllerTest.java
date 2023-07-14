package com.fasilkom.pengumpulmbkm;

import com.fasilkom.pengumpulmbkm.controller.DosenController;
import com.fasilkom.pengumpulmbkm.model.response.LaporanResponse;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.LaporanService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
public class DosenControllerTest {
    @InjectMocks
    private DosenController dosenController;

    @Mock
    private LaporanService laporanService;

    @Mock
    private UsersService usersService;

    @Mock
    private DosenService dosenService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void testVerifikasiLaporanTrue() {

    }
}
