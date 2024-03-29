package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LaporanService {

    Laporan findByLaporanId(Integer laporanId);

    void saveLaporan(Laporan laporan);

    List<Laporan> findLaporanByUserId(Integer userId);

    List<Laporan> findLaporanByDosenId(Integer dosenId);

    List<Laporan> getAllLaporan();

    void updateLaporan(Laporan laporan);

}
