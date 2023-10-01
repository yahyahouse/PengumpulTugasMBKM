package com.fasilkom.pengumpulmbkm;

import com.fasilkom.pengumpulmbkm.controller.AdminController;
import com.fasilkom.pengumpulmbkm.model.response.LaporanResponse;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.model.response.TugasAkhirResponse;
import com.fasilkom.pengumpulmbkm.model.response.UsersResponse;
import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.LaporanService;
import com.fasilkom.pengumpulmbkm.service.TugasAkhirService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {
    @InjectMocks
    private AdminController adminController;
    @Mock
    private DosenService dosenService;
    @Mock
    private UsersService usersService;
    @Mock
    private LaporanService laporanService;
    @Mock
    private TugasAkhirService tugasAkhirService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testAddDosen() throws Exception {
// Set up the test scenario
        Integer userId = 123;
        when(usersService.findByUserId(userId)).thenReturn(new Users());
        when(dosenService.getDosenByUserId(userId)).thenReturn(null);

        // Call the method under test
        ResponseEntity<MessageResponse> response = adminController.addDosen(userId);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully Added Lecturer", response.getBody().getMessage());
    }

    @Test
    void testAddDosen_WhenLecturerAlreadyExists() {
        // Set up the test scenario
        Integer userId = 123;
        when(usersService.findByUserId(userId)).thenReturn(new Users());
        when(dosenService.getDosenByUserId(userId)).thenReturn(new Dosen());

        // Call the method under test
        ResponseEntity<MessageResponse> response = adminController.addDosen(userId);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("the lecturer already exists in the database!!", response.getBody().getMessage());
    }

    @Test
    void testAddDosen_WhenUserNotFound() {
        // Set up the test scenario
        Integer userId = 123;
        when(usersService.findByUserId(userId)).thenReturn(null);

        // Call the method under test
        ResponseEntity<MessageResponse> response = adminController.addDosen(userId);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("cannot find user!!", response.getBody().getMessage());
    }

    @Test
    void testDeleteDosen_SuccessfullyDeleted() {
        // Set up the test scenario
        Integer dosenId = 1;
        when(dosenService.existsDosenByDosenId(dosenId)).thenReturn(true);
        when(tugasAkhirService.getTugasAkhirByDosenId(dosenId)).thenReturn(Collections.emptyList());
        when(laporanService.findLaporanByDosenId(dosenId)).thenReturn(Collections.emptyList());
        // Call the method under test
        ResponseEntity<MessageResponse> response = adminController.deleteDosen(dosenId);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully delete Lecturer", response.getBody().getMessage());
        verify(dosenService).deletDosenByDosenId(dosenId);
    }

    @Test
    void testDeleteDosen_FailedToDelete() {
        // Set up the test scenario
        Integer dosenId = 123;
        doThrow(new RuntimeException()).when(dosenService).deletDosenByDosenId(dosenId);

        // Call the method under test
        ResponseEntity<MessageResponse> response = adminController.deleteDosen(dosenId);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Not Found", response.getBody().getMessage());
    }

    @Test
    void testGetAllUsers() {
        // Set up the test scenario
        Users user1 = new Users();
        user1.setUserId(1);
        user1.setUsername("user1");
        Users user2 = new Users();
        user2.setUserId(2);
        user2.setUsername("user2");
        List<Users> usersList = Arrays.asList(user1, user2);

        when(usersService.getAllUsers()).thenReturn(usersList);

        // Call the method under test
        ResponseEntity<List<UsersResponse>> response = adminController.getAllUsers();

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UsersResponse> responseBody = response.getBody();
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
        users.setUserId(1);
        Dosen dosen = new Dosen();
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
        ResponseEntity<List<LaporanResponse>> response = adminController.getAllLaporan();

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<LaporanResponse> responseBody = response.getBody();
        assertEquals(laporanList.size(), responseBody.size());
        assertEquals(laporan1.getLaporanId(), responseBody.get(0).getLaporanId());
        assertEquals(laporan2.getLaporanId(), responseBody.get(1).getLaporanId());
    }

    @Test
    public void testGetAllTugasAkhir() throws IOException {
        // Mock data
        Dosen dosen = new Dosen();
        dosen.setDosenId(1);
        Users users = new Users();
        users.setUserId(1);
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
        ResponseEntity<List<TugasAkhirResponse>> responseEntity = adminController.getAllTugasAkhir();

        // Verify response
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        List<TugasAkhirResponse> tugasAkhirResponseList = responseEntity.getBody();
        Assertions.assertNotNull(tugasAkhirResponseList);
        Assertions.assertEquals(2, tugasAkhirResponseList.size());

        // Verify response data
        List<TugasAkhirResponse> tugasAkhirResponses = responseEntity.getBody();
        assertEquals(tugasAkhirList.size(), tugasAkhirResponses.size());
    }

    @Test
    public void testGetDetailTugasAkhirById() {
        // Membuat objek TugasAkhir simulasi
        Users users = new Users();
        users.setUserId(1);
        Dosen dosen = new Dosen();
        dosen.setDosenId(1);
        TugasAkhir tugasAkhir = new TugasAkhir();
        Program program = new Program();
        program.setProgramId(1);
        tugasAkhir.setUserId(users);
        tugasAkhir.setDosenId(dosen);
        tugasAkhir.setTugasAkhirId(1);
        tugasAkhir.setProgramId(program);
        // Set properti lainnya sesuai kebutuhan

        // Menyiapkan respons yang diharapkan
        TugasAkhirResponse expectedResponse = new TugasAkhirResponse(tugasAkhir);
        ResponseEntity<TugasAkhirResponse> expectedEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        // Mengatur behavior service
        when(tugasAkhirService.findByTugasAkhirId(1)).thenReturn(tugasAkhir);

        // Memanggil metode yang akan diuji
        ResponseEntity<TugasAkhirResponse> actualEntity = adminController.getDetailTugasAkhirById(1);

        // Memeriksa apakah metode tugasAkhirService.findByTugasAkhirId() dipanggil dengan benar
        verify(tugasAkhirService).findByTugasAkhirId(1);

        // Memeriksa respons yang diterima
        assertEquals(expectedEntity.getStatusCode(), actualEntity.getStatusCode());
        assertEquals(expectedEntity.getBody(), actualEntity.getBody());
    }

    @Test
    public void testGetDetailLaporanById() {
        // Membuat objek Laporan simulasi
        Users users = new Users();
        users.setUserId(1);
        Dosen dosen = new Dosen();
        dosen.setDosenId(1);
        Laporan laporan = new Laporan();
        Program program = new Program();
        program.setProgramId(1);
        laporan.setUserId(users);
        laporan.setDosenId(dosen);
        laporan.setLaporanId(1);
        laporan.setProgramId(program);
        // Set properti lainnya sesuai kebutuhan

        // Menyiapkan respons yang diharapkan
        LaporanResponse expectedResponse = new LaporanResponse(laporan);
        ResponseEntity<LaporanResponse> expectedEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        // Mengatur behavior service
        when(laporanService.findByLaporanId(1)).thenReturn(laporan);

        // Memanggil metode yang akan diuji
        ResponseEntity<LaporanResponse> actualEntity = adminController.getDetailLaporanById(1);

        // Memeriksa apakah metode laporanService.findByLaporanId() dipanggil dengan benar
        verify(laporanService).findByLaporanId(1);

        // Memeriksa respons yang diterima
        assertEquals(expectedEntity.getStatusCode(), actualEntity.getStatusCode());
        assertEquals(expectedEntity.getBody(), actualEntity.getBody());
    }

}
