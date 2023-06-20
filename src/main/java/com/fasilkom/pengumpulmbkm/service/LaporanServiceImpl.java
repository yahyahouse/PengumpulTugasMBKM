package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.repository.LaporanRepository;
import com.fasilkom.pengumpulmbkm.repository.TugasAkhirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
