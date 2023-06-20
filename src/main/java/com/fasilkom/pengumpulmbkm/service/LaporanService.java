package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import org.springframework.stereotype.Service;

@Service
public interface LaporanService {

    Laporan findByLaporanId (Integer laporanId);
    void saveLaporan(Laporan laporan);
}
