package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.repository.DosenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DosenServiceTest {

    @Mock
    private DosenRepository dosenRepository;

    @InjectMocks
    private DosenServiceImpl dosenService;

    private Dosen mockDosen;

    @BeforeEach
    void setUp() {
        mockDosen = new Dosen();
        mockDosen.setDosenId("dosen-123");
    }

    @Test
    void testSaveDosen() {
        dosenService.saveDosen(mockDosen);
        verify(dosenRepository, times(1)).save(mockDosen);
    }

    @Test
    void testGetDosenByUserId() {
        when(dosenRepository.findDosenByUserId("user-123")).thenReturn(mockDosen);
        Dosen result = dosenService.getDosenByUserId("user-123");
        assertNotNull(result);
        assertEquals("dosen-123", result.getDosenId());
    }

    @Test
    void testGetDosenByDosenId() {
        when(dosenRepository.findDosenByDosenId("dosen-123")).thenReturn(mockDosen);
        Dosen result = dosenService.getDosenByDosenId("dosen-123");
        assertNotNull(result);
        assertEquals("dosen-123", result.getDosenId());
    }

    @Test
    void testDeletDosenByDosenId() {
        dosenService.deletDosenByDosenId("dosen-123");
        verify(dosenRepository, times(1)).deleteDosenByDosenId("dosen-123");
    }

    @Test
    void testGetAllDosen() {
        List<Dosen> list = Arrays.asList(mockDosen);
        when(dosenRepository.getAllDosen()).thenReturn(list);
        List<Dosen> result = dosenService.getAllDosen();
        assertEquals(1, result.size());
    }

    @Test
    void testExistsDosenByDosenId() {
        when(dosenRepository.existsDosenByDosenId("dosen-123")).thenReturn(true);
        assertTrue(dosenService.existsDosenByDosenId("dosen-123"));
    }

    @Test
    void testExistsDosenByUserId() {
        when(dosenRepository.existsDosenByUserId("user-123")).thenReturn(true);
        assertTrue(dosenService.existsDosenByUserId("user-123"));
    }
}
