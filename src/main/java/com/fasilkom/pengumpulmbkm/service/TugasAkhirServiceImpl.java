package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.repository.TugasAkhirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class TugasAkhirServiceImpl implements TugasAkhirService{
    @Autowired
    TugasAkhirRepository tugasAkhirRepository;
    @Override
    public TugasAkhir findByTugasAkhirId(Integer tugasAkhirId) {
        return tugasAkhirRepository.findByTugasAkhirId(tugasAkhirId);
    }

    @Override
    public void saveTugasAkhir(TugasAkhir tugasAkhir) {
        tugasAkhirRepository.save(tugasAkhir);

    }

    @Override
    public void updateTugasAkhir(Integer tugasAkhirId, byte[] laporanTugasAkhir, byte[] lembarPengesahan, byte[] nilai,
                                 byte[] sertifikat, Timestamp waktuUpdate) {
        tugasAkhirRepository.updateTugasAkhirsById(tugasAkhirId,laporanTugasAkhir,lembarPengesahan,nilai,sertifikat,waktuUpdate);
    }

    @Override
    public void updateCatatanInTugasAkhir(TugasAkhir tugasAkhir) {
        tugasAkhirRepository.updateTugasAkhirsForCatatanAndVerifikasiById(
                tugasAkhir.getTugasAkhirId(),
                tugasAkhir.getCatatan(),
                tugasAkhir.getWaktuUpdate());
    }

    @Override
    public List<TugasAkhir> getTugasAkhirByUserId(Integer dosenId) {
        return tugasAkhirRepository.getTugasAkhirByUserId(dosenId);
    }

    @Override
    public List<TugasAkhir> getTugasAkhirByDosenId(Integer dosenId) {
        return tugasAkhirRepository.getTugasAkhirByDosenId(dosenId);
    }

    @Override
    public List<TugasAkhir> getAllTugasAkhir() {
        return tugasAkhirRepository.getAllTugasAkhir();
    }
}
