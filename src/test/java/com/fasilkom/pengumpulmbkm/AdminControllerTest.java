package com.fasilkom.pengumpulmbkm;

import com.fasilkom.pengumpulmbkm.controller.AdminController;
import com.fasilkom.pengumpulmbkm.model.response.*;
import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.LaporanService;
import com.fasilkom.pengumpulmbkm.service.TugasAkhirService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import com.fasilkom.pengumpulmbkm.util.CommonConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {
    @InjectMocks
    private AdminController adminController;
    @MockitoBean
    private DosenService dosenService;
    @MockitoBean
    private UsersService usersService;
    @MockitoBean
    private LaporanService laporanService;
    @MockitoBean
    private TugasAkhirService tugasAkhirService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testAddDosen() {
        // Set up the test scenario
        String userId = "123";
        when(usersService.findByUserId(userId)).thenReturn(new Users());
        when(dosenService.getDosenByUserId(userId)).thenReturn(null);

        // Call the method under test
        ResponseEntity<BaseResponse> response = adminController.addDosen(userId);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully Added Lecturer", response.getBody().getTransaction().getMessage());
    }

    @Test
    void testAddDosen_WhenLecturerAlreadyExists() {
        // Set up the test scenario
        String userId = "123";
        when(usersService.findByUserId(userId)).thenReturn(new Users());
        when(dosenService.getDosenByUserId(userId)).thenReturn(new Dosen());

        // Call the method under test
        ResponseEntity<BaseResponse> response = adminController.addDosen(userId);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("the lecturer already exists in the database!!", response.getBody().getTransaction().getMessage());
    }

    @Test
    void testAddDosen_WhenUserNotFound() {
        // Set up the test scenario
        String userId = "123";
        when(usersService.findByUserId(userId)).thenReturn(null);

        // Call the method under test
        ResponseEntity<BaseResponse> response = adminController.addDosen(userId);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("cannot find user!!", response.getBody().getTransaction().getMessage());
    }

    @Test
    void testDeleteDosen_SuccessfullyDeleted() {
        // Set up the test scenario
        String dosenId = "1";
        when(dosenService.existsDosenByDosenId(dosenId)).thenReturn(true);
        when(tugasAkhirService.getTugasAkhirByDosenId(dosenId)).thenReturn(Collections.emptyList());
        when(laporanService.findLaporanByDosenId(dosenId)).thenReturn(Collections.emptyList());
        // Call the method under test
        ResponseEntity<BaseResponse> response = adminController.deleteDosen(dosenId);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully delete Lecturer", response.getBody().getTransaction().getMessage());
        verify(dosenService).deletDosenByDosenId(dosenId);
    }

    @Test
    void testDeleteDosen_NotEmptyTasks() {
        String dosenId = "1";
        when(dosenService.existsDosenByDosenId(dosenId)).thenReturn(true);
        when(tugasAkhirService.getTugasAkhirByDosenId(dosenId)).thenReturn(Collections.singletonList(new TugasAkhir()));

        ResponseEntity<BaseResponse> response = adminController.deleteDosen(dosenId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Lecturer cannot be deleted because they are assigned to a Final Project or Report", response.getBody().getTransaction().getMessage());
    }

    @Test
    void testDeleteDosen_NotFound() {
        String dosenId = "1";
        when(dosenService.existsDosenByDosenId(dosenId)).thenReturn(false);

        ResponseEntity<BaseResponse> response = adminController.deleteDosen(dosenId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(CommonConstant.NOT_FOUND, response.getBody().getTransaction().getMessage());
    }

    @Test
    void testDeleteDosen_FailedToDelete() {
        // Set up the test scenario
        String dosenId = "123";
        when(dosenService.existsDosenByDosenId(dosenId)).thenReturn(true);
        doThrow(new RuntimeException()).when(dosenService).deletDosenByDosenId(dosenId);

        // Call the method under test
        ResponseEntity<BaseResponse> response = adminController.deleteDosen(dosenId);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(CommonConstant.NOT_FOUND, response.getBody().getTransaction().getMessage());
    }

    @Test
    void testGetAllUsers() {
        // Set up the test scenario
        Users user1 = new Users();
        user1.setUserId("1");
        user1.setUsername("user1");
        Users user2 = new Users();
        user2.setUserId("2");
        user2.setUsername("user2");
        List<Users> usersList = Arrays.asList(user1, user2);

        when(usersService.getAllUsers()).thenReturn(usersList);

        // Call the method under test
        ResponseEntity<BaseResponse> response = adminController.getAllUsers();

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UsersResponse> responseBody = (List<UsersResponse>) response.getBody().getData();
        assertEquals(usersList.size(), responseBody.size());
        assertEquals(user1.getUserId(), responseBody.get(0).getUserId());
        assertEquals(user1.getUsername(), responseBody.get(0).getUsername());
        assertEquals(user2.getUserId(), responseBody.get(1).getUserId());
        assertEquals(user2.getUsername(), responseBody.get(1).getUsername());
    }

    @Test
    void testGetAllLaporan() {
        // Set up the test scenario
        Users users = new Users();
        users.setUserId("1");
        Dosen dosen = new Dosen();
        dosen.setDosenId("1");
        Laporan laporan1 = new Laporan();
        Program program = new Program();
        program.setProgramId(1);
        laporan1.setDosenId(dosen);
        laporan1.setUserId(users);
        laporan1.setLaporanId(1);
        laporan1.setProgramId(program);
        Laporan laporan2 = new Laporan();
        laporan2.setDosenId(dosen);
        laporan2.setUserId(users);
        laporan2.setLaporanId(2);
        laporan2.setProgramId(program);
        List<Laporan> laporanList = Arrays.asList(laporan1, laporan2);

        when(laporanService.getAllLaporan()).thenReturn(laporanList);

        // Call the method under test
        ResponseEntity<BaseResponse> response = adminController.getAllLaporan();

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<LaporanResponse> responseBody = (List<LaporanResponse>) response.getBody().getData();
        assertEquals(laporanList.size(), responseBody.size());
        assertEquals(laporan1.getLaporanId(), responseBody.get(0).getLaporanId());
        assertEquals(laporan2.getLaporanId(), responseBody.get(1).getLaporanId());
    }

    @Test
    void testGetAllTugasAkhir() {
        // Mock data
        Dosen dosen = new Dosen();
        dosen.setDosenId("1");
        Users users = new Users();
        users.setUserId("1");
        TugasAkhir tugasAkhir1 = new TugasAkhir();
        Program program = new Program();
        program.setProgramId(1);
        tugasAkhir1.setUserId(users);
        tugasAkhir1.setTugasAkhirId(1);
        tugasAkhir1.setDosenId(dosen);
        tugasAkhir1.setProgramId(program);

        TugasAkhir tugasAkhir2 = new TugasAkhir();
        tugasAkhir2.setUserId(users);
        tugasAkhir2.setTugasAkhirId(2);
        tugasAkhir2.setDosenId(dosen);
        tugasAkhir2.setProgramId(program);

        List<TugasAkhir> tugasAkhirList = Arrays.asList(tugasAkhir1, tugasAkhir2);

        // Mock service
        Mockito.when(tugasAkhirService.getAllTugasAkhir()).thenReturn(tugasAkhirList);

        // Invoke controller method
        ResponseEntity<BaseResponse> responseEntity = adminController.getAllTugasAkhir();

        // Verify response
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        List<TugasAkhirResponse> tugasAkhirResponseList = (List<TugasAkhirResponse>) responseEntity.getBody().getData();
        Assertions.assertNotNull(tugasAkhirResponseList);
        Assertions.assertEquals(2, tugasAkhirResponseList.size());
    }

    @Test
    void testGetDetailTugasAkhirById() {
        // Membuat objek TugasAkhir simulasi
        Users users = new Users();
        users.setUserId("1");
        Dosen dosen = new Dosen();
        dosen.setDosenId("1");
        TugasAkhir tugasAkhir = new TugasAkhir();
        Program program = new Program();
        program.setProgramId(1);
        tugasAkhir.setUserId(users);
        tugasAkhir.setDosenId(dosen);
        tugasAkhir.setTugasAkhirId(1);
        tugasAkhir.setProgramId(program);

        // Menyiapkan data yang diharapkan
        TugasAkhirResponse expectedResponse = new TugasAkhirResponse(tugasAkhir);

        // Mengatur behavior service
        when(tugasAkhirService.findByTugasAkhirId(1)).thenReturn(tugasAkhir);

        // Memanggil metode yang akan diuji
        ResponseEntity<BaseResponse> actualEntity = adminController.getDetailTugasAkhirById(1);

        // Memeriksa apakah metode tugasAkhirService.findByTugasAkhirId() dipanggil dengan benar
        verify(tugasAkhirService).findByTugasAkhirId(1);

        // Memeriksa respons yang diterima
        assertEquals(HttpStatus.OK, actualEntity.getStatusCode());
        assertEquals(expectedResponse, actualEntity.getBody().getData());
    }

    @Test
    void testGetDetailLaporanById() {
        // Membuat objek Laporan simulasi
        Users users = new Users();
        users.setUserId("1");
        Dosen dosen = new Dosen();
        dosen.setDosenId("1");
        Laporan laporan = new Laporan();
        Program program = new Program();
        program.setProgramId(1);
        laporan.setUserId(users);
        laporan.setDosenId(dosen);
        laporan.setLaporanId(1);
        laporan.setProgramId(program);

        // Menyiapkan data yang diharapkan
        LaporanResponse expectedResponse = new LaporanResponse(laporan);

        // Mengatur behavior service
        when(laporanService.findByLaporanId(1)).thenReturn(laporan);

        // Memanggil metode yang akan diuji
        ResponseEntity<BaseResponse> actualEntity = adminController.getDetailLaporanById(1);

        // Memeriksa apakah metode laporanService.findByLaporanId() dipanggil dengan benar
        verify(laporanService).findByLaporanId(1);

        // Memeriksa respons yang diterima
        assertEquals(HttpStatus.OK, actualEntity.getStatusCode());
        assertEquals(expectedResponse, actualEntity.getBody().getData());
    }

}
