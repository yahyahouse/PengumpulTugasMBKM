package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.repository.LaporanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LaporanServiceTest {

    @Mock
    private LaporanRepository laporanRepository;

    @Mock
    private DosenService dosenService;

    @Mock
    private ProgramService programService;

    @InjectMocks
    private LaporanServiceImpl laporanService;

    private Users mockUser;
    private Dosen mockDosen;
    private Program mockProgram;
    private Laporan mockLaporan;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId("user-123");

        mockDosen = new Dosen();
        mockDosen.setDosenId("dosen-456");

        mockProgram = new Program();
        mockProgram.setProgramId(1);

        mockLaporan = new Laporan();
        mockLaporan.setLaporanId(1);
        mockLaporan.setUserId(mockUser);
        mockLaporan.setDosenId(mockDosen);
        mockLaporan.setProgramId(mockProgram);
    }

    @Test
    void testFindByLaporanId() {
        when(laporanRepository.findByLaporanId(1)).thenReturn(mockLaporan);
        Laporan result = laporanService.findByLaporanId(1);
        assertNotNull(result);
        assertEquals(1, result.getLaporanId());
    }

    @Test
    void testSaveLaporan() {
        laporanService.saveLaporan(mockLaporan);
        verify(laporanRepository, times(1)).save(mockLaporan);
    }

    @Test
    void testFindLaporanByUserId() {
        List<Laporan> list = Arrays.asList(mockLaporan);
        when(laporanRepository.getLaporanByUserId("user-123")).thenReturn(list);
        List<Laporan> result = laporanService.findLaporanByUserId("user-123");
        assertEquals(1, result.size());
    }

    @Test
    void testFindLaporanByDosenId() {
        List<Laporan> list = Arrays.asList(mockLaporan);
        when(laporanRepository.getLaporanByDosenId("dosen-456")).thenReturn(list);
        List<Laporan> result = laporanService.findLaporanByDosenId("dosen-456");
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllLaporan() {
        List<Laporan> list = Arrays.asList(mockLaporan);
        when(laporanRepository.getAllLaporan()).thenReturn(list);
        List<Laporan> result = laporanService.getAllLaporan();
        assertEquals(1, result.size());
    }

    @Test
    void testUpdateLaporan() {
        laporanService.updateLaporan(mockLaporan);
        verify(laporanRepository, times(1)).save(mockLaporan);
    }

    @Test
    void testUploadLaporan_Success() {
        when(dosenService.getDosenByDosenId("dosen-456")).thenReturn(mockDosen);
        when(programService.findByProgramid(1)).thenReturn(mockProgram);
        when(laporanRepository.save(any(Laporan.class))).thenReturn(mockLaporan);

        Laporan result = laporanService.uploadLaporan("dosen-456", "Content", 1, mockUser);
        assertNotNull(result);
        verify(laporanRepository).save(any(Laporan.class));
    }

    @Test
    void testUploadLaporan_DosenNotFound() {
        when(dosenService.getDosenByDosenId("dosen-456")).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> 
            laporanService.uploadLaporan("dosen-456", "Content", 1, mockUser));
    }

    @Test
    void testUploadLaporan_ProgramNotFound() {
        when(dosenService.getDosenByDosenId("dosen-456")).thenReturn(mockDosen);
        when(programService.findByProgramid(1)).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> 
            laporanService.uploadLaporan("dosen-456", "Content", 1, mockUser));
    }

    @Test
    void testUpdateLaporanBusinessLogic_Success() {
        when(laporanRepository.findByLaporanId(1)).thenReturn(mockLaporan);
        when(programService.findByProgramid(1)).thenReturn(mockProgram);
        when(laporanRepository.save(any(Laporan.class))).thenReturn(mockLaporan);

        Laporan result = laporanService.updateLaporanBusinessLogic(1, "Updated Content", 1, mockUser);
        assertNotNull(result);
        assertEquals("Updated Content", result.getLaporan());
    }

    @Test
    void testUpdateLaporanBusinessLogic_LaporanNotFound() {
        when(laporanRepository.findByLaporanId(1)).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> 
            laporanService.updateLaporanBusinessLogic(1, "Content", 1, mockUser));
    }

    @Test
    void testUpdateLaporanBusinessLogic_AccessDenied() {
        Users anotherUser = new Users();
        anotherUser.setUserId("another-user");
        when(laporanRepository.findByLaporanId(1)).thenReturn(mockLaporan);
        when(programService.findByProgramid(1)).thenReturn(mockProgram);

        assertThrows(AccessDeniedException.class, () -> 
            laporanService.updateLaporanBusinessLogic(1, "Content", 1, anotherUser));
    }
}
