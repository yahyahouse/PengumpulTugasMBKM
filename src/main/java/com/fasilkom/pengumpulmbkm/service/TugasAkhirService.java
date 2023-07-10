package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public interface TugasAkhirService {

    TugasAkhir findByTugasAkhirId(Integer tugasAkhirId);

    void saveTugasAkhir(TugasAkhir tugasAkhir);

    void updateTugasAkhir(Integer tugasAkhirId, byte[] laporanTugasAkhir, byte[] lembarPengesahan, byte[] nilai,
                          byte[] sertifikat, Timestamp waktuUpdate);

    void updateCatatanInTugasAkhir(TugasAkhir tugasAkhir);

    List<TugasAkhir> getTugasAkhirByUserId(Integer dosenId);

    List<TugasAkhir> getTugasAkhirByDosenId(Integer dosenId);

    List<TugasAkhir> getAllTugasAkhir();
}
