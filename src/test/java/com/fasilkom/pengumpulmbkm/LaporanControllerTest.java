package com.fasilkom.pengumpulmbkm;

import com.fasilkom.pengumpulmbkm.controller.LaporanController;
import com.fasilkom.pengumpulmbkm.model.response.BaseResponse;
import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.LaporanService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import com.yahya.commonlogger.StructuredLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class LaporanControllerTest {

    @Autowired
    private LaporanController laporanController;

    @MockitoBean
    private LaporanService laporanService;

    @MockitoBean
    private UsersService usersService;

    @MockitoBean(answers = Answers.RETURNS_DEEP_STUBS)
    private StructuredLogger structuredLogger;

    private Users mockUser;
    private Laporan mockLaporan;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId("user-123");
        mockUser.setUsername("testuser");

        Dosen mockDosen = new Dosen();
        mockDosen.setDosenId("dosen-123");

        Program mockProgram = new Program();
        mockProgram.setProgramId(1);

        mockLaporan = new Laporan();
        mockLaporan.setLaporanId(1);
        mockLaporan.setUserId(mockUser);
        mockLaporan.setDosenId(mockDosen);
        mockLaporan.setProgramId(mockProgram);

        auth = new UsernamePasswordAuthenticationToken("testuser", "password");
    }

    @Test
    void testUploadLaporan_Success() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(laporanService.uploadLaporan(anyString(), anyString(), anyInt(), any())).thenReturn(mockLaporan);

        ResponseEntity<BaseResponse> response = laporanController.uploadLaporan("dosen-123", "Content", 1, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUploadLaporan_IllegalArgumentException() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(laporanService.uploadLaporan(anyString(), anyString(), anyInt(), any())).thenThrow(new IllegalArgumentException("Invalid"));

        ResponseEntity<BaseResponse> response = laporanController.uploadLaporan("dosen-123", "Content", 1, auth);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUploadLaporan_GeneralException() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(laporanService.uploadLaporan(anyString(), anyString(), anyInt(), any())).thenThrow(new RuntimeException("Oops"));

        ResponseEntity<BaseResponse> response = laporanController.uploadLaporan("dosen-123", "Content", 1, auth);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testUpdateLaporan_Success() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(laporanService.updateLaporanBusinessLogic(anyInt(), anyString(), anyInt(), any())).thenReturn(mockLaporan);

        ResponseEntity<BaseResponse> response = laporanController.updatelaporan(1, "Updated Content", 1, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateLaporan_IllegalArgumentException() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(laporanService.updateLaporanBusinessLogic(anyInt(), anyString(), anyInt(), any())).thenThrow(new IllegalArgumentException("Not Found"));

        ResponseEntity<BaseResponse> response = laporanController.updatelaporan(1, "Content", 1, auth);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateLaporan_AccessDeniedException() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(laporanService.updateLaporanBusinessLogic(anyInt(), anyString(), anyInt(), any())).thenThrow(new AccessDeniedException("Denied"));

        ResponseEntity<BaseResponse> response = laporanController.updatelaporan(1, "Content", 1, auth);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateLaporan_GeneralException() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(laporanService.updateLaporanBusinessLogic(anyInt(), anyString(), anyInt(), any())).thenThrow(new RuntimeException("Oops"));

        ResponseEntity<BaseResponse> response = laporanController.updatelaporan(1, "Content", 1, auth);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetLaporanByUserId() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(laporanService.findLaporanByUserId(anyString())).thenReturn(Collections.singletonList(mockLaporan));

        ResponseEntity<BaseResponse> response = laporanController.getLaporanByUserId(auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> data = (List<?>) response.getBody().getData();
        assertEquals(1, data.size());
    }

    @Test
    void testGetLaporanById_Success() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(laporanService.findByLaporanId(1)).thenReturn(mockLaporan);

        ResponseEntity<BaseResponse> response = laporanController.getLaporanByid(1, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetLaporanById_NotFound() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(laporanService.findByLaporanId(1)).thenReturn(null);

        ResponseEntity<BaseResponse> response = laporanController.getLaporanByid(1, auth);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetLaporanById_Forbidden() {
        Users anotherUser = new Users();
        anotherUser.setUserId("another-user");
        when(usersService.findByUsername(anyString())).thenReturn(anotherUser);
        when(laporanService.findByLaporanId(1)).thenReturn(mockLaporan);

        ResponseEntity<BaseResponse> response = laporanController.getLaporanByid(1, auth);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGetLaporanById_GeneralException() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(laporanService.findByLaporanId(anyInt())).thenThrow(new RuntimeException("Oops"));

        ResponseEntity<BaseResponse> response = laporanController.getLaporanByid(1, auth);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
