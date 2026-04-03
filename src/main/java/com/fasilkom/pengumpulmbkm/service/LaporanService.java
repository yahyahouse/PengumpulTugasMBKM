package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LaporanService {

    Laporan findByLaporanId(Integer laporanId);

    void saveLaporan(Laporan laporan);

    List<Laporan> findLaporanByUserId(String userId);

    List<Laporan> findLaporanByDosenId(String dosenId);

    List<Laporan> getAllLaporan();

    void updateLaporan(Laporan laporan);

    Laporan uploadLaporan(String dosenId, String laporanMBKM, Integer programId, Users user);

    Laporan updateLaporanBusinessLogic(Integer laporanId, String laporan, Integer programId, Users user);

}
