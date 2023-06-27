package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TugasAkhirService {

    TugasAkhir findByTugasAkhirId (Integer tugasAkhirId);
    void saveTugasAkhir(TugasAkhir tugasAkhir);
    void updateTugasAkhir (TugasAkhir tugasAkhir);
    List<TugasAkhir> getTugasAkhirByUserId (Integer dosenId);
    List<TugasAkhir> getTugasAkhirByDosenId (Integer dosenId);
}
