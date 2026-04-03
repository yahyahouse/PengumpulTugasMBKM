package com.fasilkom.pengumpulmbkm;

import com.fasilkom.pengumpulmbkm.controller.TugasAkhirController;
import com.fasilkom.pengumpulmbkm.model.response.BaseResponse;
import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.ProgramService;
import com.fasilkom.pengumpulmbkm.service.TugasAkhirService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import com.fasilkom.pengumpulmbkm.util.CommonConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TugasAkhirControllerTest {

    @Autowired
    private TugasAkhirController tugasAkhirController;

    @MockitoBean
    private UsersService usersService;

    @MockitoBean
    private DosenService dosenService;

    @MockitoBean
    private ProgramService programService;

    @MockitoBean
    private TugasAkhirService tugasAkhirService;

    private Users mockUser;
    private Dosen mockDosen;
    private Program mockProgram;
    private TugasAkhir mockTa;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId("user-123");
        mockUser.setUsername("testuser");

        mockDosen = new Dosen();
        mockDosen.setDosenId("dosen-123");

        mockProgram = new Program();
        mockProgram.setProgramId(1);

        mockTa = new TugasAkhir();
        mockTa.setTugasAkhirId(1);
        mockTa.setUserId(mockUser);
        mockTa.setDosenId(mockDosen);
        mockTa.setProgramId(mockProgram);

        auth = new UsernamePasswordAuthenticationToken("testuser", "password");
    }

    @Test
    void testUploadTugasAkhir_Success() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(usersService.findByUserId(anyString())).thenReturn(mockUser);
        when(dosenService.getDosenByDosenId(anyString())).thenReturn(mockDosen);
        when(programService.findByProgramid(anyInt())).thenReturn(mockProgram);

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "text/plain", "test data".getBytes());

        ResponseEntity<BaseResponse> response = tugasAkhirController.uploadTugasAkhir(
                "dosen-123", 1, file, file, file, file, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUploadTugasAkhir_DosenNotFound() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(dosenService.getDosenByDosenId(anyString())).thenReturn(null);

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "text/plain", "test data".getBytes());

        ResponseEntity<BaseResponse> response = tugasAkhirController.uploadTugasAkhir(
                "dosen-123", 1, file, file, file, file, auth);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(CommonConstant.DOSEN_NOT_FOUND, response.getBody().getTransaction().getMessage());
    }

    @Test
    void testUploadTugasAkhir_ProgramNotFound() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(dosenService.getDosenByDosenId(anyString())).thenReturn(mockDosen);
        when(programService.findByProgramid(anyInt())).thenReturn(null);

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "text/plain", "test data".getBytes());

        ResponseEntity<BaseResponse> response = tugasAkhirController.uploadTugasAkhir(
                "dosen-123", 1, file, file, file, file, auth);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(CommonConstant.PROGRAM_MBKM_NOT_FOUND, response.getBody().getTransaction().getMessage());
    }

    @Test
    void testUploadTugasAkhir_IOException() throws IOException {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(dosenService.getDosenByDosenId(anyString())).thenReturn(mockDosen);
        when(programService.findByProgramid(anyInt())).thenReturn(mockProgram);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenThrow(new IOException("Error reading file"));

        ResponseEntity<BaseResponse> response = tugasAkhirController.uploadTugasAkhir(
                "dosen-123", 1, file, file, file, file, auth);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testUpdateTugasAkhir_Success() {
        when(tugasAkhirService.findByTugasAkhirId(anyInt())).thenReturn(mockTa);
        when(programService.findByProgramid(anyInt())).thenReturn(mockProgram);
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "text/plain", "test data".getBytes());

        ResponseEntity<BaseResponse> response = tugasAkhirController.updateTugasAkhir(
                1, 1, file, file, file, file, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateTugasAkhir_NotFound() {
        when(tugasAkhirService.findByTugasAkhirId(anyInt())).thenReturn(null);

        ResponseEntity<BaseResponse> response = tugasAkhirController.updateTugasAkhir(
                1, 1, null, null, null, null, auth);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateTugasAkhir_ProgramNotFound() {
        when(tugasAkhirService.findByTugasAkhirId(anyInt())).thenReturn(mockTa);
        when(programService.findByProgramid(anyInt())).thenReturn(null);

        ResponseEntity<BaseResponse> response = tugasAkhirController.updateTugasAkhir(
                1, 1, null, null, null, null, auth);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateTugasAkhir_Forbidden() {
        Users anotherUser = new Users();
        anotherUser.setUserId("another-user");
        when(tugasAkhirService.findByTugasAkhirId(anyInt())).thenReturn(mockTa);
        when(programService.findByProgramid(anyInt())).thenReturn(mockProgram);
        when(usersService.findByUsername(anyString())).thenReturn(anotherUser);

        ResponseEntity<BaseResponse> response = tugasAkhirController.updateTugasAkhir(
                1, 1, null, null, null, null, auth);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUpdateTugasAkhir_IOException() throws IOException {
        when(tugasAkhirService.findByTugasAkhirId(anyInt())).thenReturn(mockTa);
        when(programService.findByProgramid(anyInt())).thenReturn(mockProgram);
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenThrow(new IOException("Error"));

        ResponseEntity<BaseResponse> response = tugasAkhirController.updateTugasAkhir(
                1, 1, file, file, file, file, auth);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetTugasAkhirById_Success() {
        when(tugasAkhirService.findByTugasAkhirId(anyInt())).thenReturn(mockTa);
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);

        ResponseEntity<BaseResponse> response = tugasAkhirController.getTugasAkhirByid(1, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetTugasAkhirById_NotFound() {
        when(tugasAkhirService.findByTugasAkhirId(anyInt())).thenReturn(null);

        ResponseEntity<BaseResponse> response = tugasAkhirController.getTugasAkhirByid(1, auth);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetTugasAkhirById_Forbidden() {
        Users anotherUser = new Users();
        anotherUser.setUserId("another-user");
        when(tugasAkhirService.findByTugasAkhirId(anyInt())).thenReturn(mockTa);
        when(usersService.findByUsername(anyString())).thenReturn(anotherUser);

        ResponseEntity<BaseResponse> response = tugasAkhirController.getTugasAkhirByid(1, auth);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGetTugasAkhirByUserId_NotFound() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(tugasAkhirService.getTugasAkhirByUserId(anyString())).thenReturn(null);

        ResponseEntity<BaseResponse> response = tugasAkhirController.getTugasAkhirByUserId(auth);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetTugasAkhirByUserId_Success() {
        when(usersService.findByUsername(anyString())).thenReturn(mockUser);
        when(tugasAkhirService.getTugasAkhirByUserId(anyString())).thenReturn(Collections.singletonList(mockTa));

        ResponseEntity<BaseResponse> response = tugasAkhirController.getTugasAkhirByUserId(auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> data = (List<?>) response.getBody().getData();
        assertEquals(1, data.size());
    }
}
