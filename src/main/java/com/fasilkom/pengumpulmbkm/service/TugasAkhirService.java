package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import org.springframework.stereotype.Service;

@Service
public interface TugasAkhirService {

    TugasAkhir findByTugasAkhirId (Integer tugasAkhirId);
    void saveTugasAkhir(TugasAkhir tugasAkhir);
}
