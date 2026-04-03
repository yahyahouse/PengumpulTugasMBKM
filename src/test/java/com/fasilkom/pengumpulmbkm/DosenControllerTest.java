package com.fasilkom.pengumpulmbkm;

import com.fasilkom.pengumpulmbkm.controller.DosenController;
import com.fasilkom.pengumpulmbkm.model.enumeration.EProgram;
import com.fasilkom.pengumpulmbkm.model.response.BaseResponse;
import com.fasilkom.pengumpulmbkm.model.response.LaporanResponse;
import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.LaporanService;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class DosenControllerTest {

    @Autowired
    private DosenController dosenController;

    @MockitoBean
    private LaporanService laporanService;

    @MockitoBean
    private UsersService usersService;

    @MockitoBean
    private DosenService dosenService;

    @MockitoBean
    private TugasAkhirService tugasAkhirService;

    private Users mockUser;
    private Dosen mockDosen;
    private Laporan mockLaporan;
    private Authentication auth;
    private Program mockProgram;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId("1");
        mockUser.setUsername("lecturerTest");

        mockDosen = new Dosen();
        mockDosen.setDosenId("1");
        mockDosen.setUserId(mockUser);

        mockProgram = new Program();
        mockProgram.setProgramId(1);
        mockProgram.setName(EProgram.BANGKIT);

        mockLaporan = new Laporan();
        mockLaporan.setLaporanId(1);
        mockLaporan.setDosenId(mockDosen);
        mockLaporan.setVerifikasi(false);
        mockLaporan.setUserId(mockUser);
        mockLaporan.setProgramId(mockProgram);

        auth = new UsernamePasswordAuthenticationToken("lecturerTest", "password");

        // Common mocks
        when(usersService.findByUsername("lecturerTest")).thenReturn(mockUser);
        when(dosenService.getDosenByUserId("1")).thenReturn(mockDosen);
    }

    @Test
    void testVerifikasiLaporanTrue_Success() {
        when(laporanService.findByLaporanId(1)).thenReturn(mockLaporan);

        ResponseEntity<BaseResponse> response = dosenController.verifikasiLaporanTrue(1, auth);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        LaporanResponse body = (LaporanResponse) response.getBody().getData();
        assertEquals(true, body.getVerifikasi());
    }

    @Test
    void testVerifikasiLaporanTrue_NotFound() {
        when(laporanService.findByLaporanId(1)).thenReturn(null);

        ResponseEntity<BaseResponse> response = dosenController.verifikasiLaporanTrue(1, auth);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(CommonConstant.NOT_FOUND, response.getBody().getTransaction().getMessage());
    }

    @Test
    void testVerifikasiLaporanTrue_Forbidden() {
        Dosen anotherDosen = new Dosen();
        anotherDosen.setDosenId("2");

        Laporan otherLaporan = new Laporan();
        otherLaporan.setLaporanId(1);
        otherLaporan.setDosenId(anotherDosen);
        otherLaporan.setUserId(mockUser);
        otherLaporan.setProgramId(mockProgram);

        when(laporanService.findByLaporanId(1)).thenReturn(otherLaporan);

        ResponseEntity<BaseResponse> response = dosenController.verifikasiLaporanTrue(1, auth);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(CommonConstant.AKSES_DITOLAK, response.getBody().getTransaction().getMessage());
    }

    @Test
    void testGetReports_Success() {
        when(laporanService.findLaporanByDosenId("1")).thenReturn(Collections.singletonList(mockLaporan));

        ResponseEntity<BaseResponse> response = dosenController.getLaporanByUserId(auth);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<LaporanResponse> body = (List<LaporanResponse>) response.getBody().getData();
        assertEquals(1, body.size());
    }
}
