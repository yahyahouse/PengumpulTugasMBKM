package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.repository.LaporanRepository;
import com.fasilkom.pengumpulmbkm.repository.TugasAkhirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LaporanServiceImpl implements LaporanService{
    @Autowired
    LaporanRepository laporanRepository;


    @Override
    public Laporan findByLaporanId(Integer laporanId) {
        return laporanRepository.findByLaporanId(laporanId);
    }

    @Override
    public void saveLaporan(Laporan laporan) {
        laporanRepository.save(laporan);
    }

    @Override
    public List<Laporan> findLaporanByUserId(Integer userId) {
        return laporanRepository.getLaporanByUserId(userId);
    }

    @Override
    public List<Laporan> findLaporanByDosenId(Integer dosenId) {
        return laporanRepository.getLaporanByDosenId(dosenId);
    }

    @Override
    public List<Laporan> getAllLaporan() {
        return laporanRepository.getAllLaporan();
    }

    @Override
    public void updateLaporan(Laporan laporan) {
        laporanRepository.save(laporan);
    }
}
