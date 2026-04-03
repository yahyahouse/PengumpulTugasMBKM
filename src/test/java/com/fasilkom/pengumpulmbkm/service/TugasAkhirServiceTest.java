package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.repository.TugasAkhirRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TugasAkhirServiceTest {

    @Mock
    private TugasAkhirRepository tugasAkhirRepository;

    @InjectMocks
    private TugasAkhirServiceImpl tugasAkhirService;

    private TugasAkhir mockTa;

    @BeforeEach
    void setUp() {
        mockTa = new TugasAkhir();
        mockTa.setTugasAkhirId(1);
        mockTa.setCatatan("Some notes");
        mockTa.setWaktuUpdate(Timestamp.valueOf(LocalDateTime.now()));
    }

    @Test
    void testFindByTugasAkhirId() {
        when(tugasAkhirRepository.findByTugasAkhirId(1)).thenReturn(mockTa);
        TugasAkhir result = tugasAkhirService.findByTugasAkhirId(1);
        assertNotNull(result);
        assertEquals(1, result.getTugasAkhirId());
    }

    @Test
    void testSaveTugasAkhir() {
        tugasAkhirService.saveTugasAkhir(mockTa);
        verify(tugasAkhirRepository, times(1)).save(mockTa);
    }

    @Test
    void testUpdateTugasAkhir() {
        byte[] data = new byte[]{1, 2, 3};
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        tugasAkhirService.updateTugasAkhir(1, data, data, data, data, now);
        verify(tugasAkhirRepository).updateTugasAkhirsById(1, data, data, data, data, now);
    }

    @Test
    void testUpdateCatatanInTugasAkhir() {
        tugasAkhirService.updateCatatanInTugasAkhir(mockTa);
        verify(tugasAkhirRepository).updateTugasAkhirsForCatatanAndVerifikasiById(
                mockTa.getTugasAkhirId(),
                mockTa.getCatatan(),
                mockTa.getWaktuUpdate());
    }

    @Test
    void testGetTugasAkhirByUserId() {
        List<TugasAkhir> list = Arrays.asList(mockTa);
        when(tugasAkhirRepository.getTugasAkhirByUserId("user-123")).thenReturn(list);
        List<TugasAkhir> result = tugasAkhirService.getTugasAkhirByUserId("user-123");
        assertEquals(1, result.size());
    }

    @Test
    void testGetTugasAkhirByDosenId() {
        List<TugasAkhir> list = Arrays.asList(mockTa);
        when(tugasAkhirRepository.getTugasAkhirByDosenId("dosen-456")).thenReturn(list);
        List<TugasAkhir> result = tugasAkhirService.getTugasAkhirByDosenId("dosen-456");
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllTugasAkhir() {
        List<TugasAkhir> list = Arrays.asList(mockTa);
        when(tugasAkhirRepository.getAllTugasAkhir()).thenReturn(list);
        List<TugasAkhir> result = tugasAkhirService.getAllTugasAkhir();
        assertEquals(1, result.size());
    }
}
