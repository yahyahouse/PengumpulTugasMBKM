package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.repository.TugasAkhirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void updateTugasAkhir(TugasAkhir tugasAkhir) {
        tugasAkhirRepository.save(tugasAkhir);
    }

    @Override
    public List<TugasAkhir> getTugasAkhirByUserId(Integer tugasAkhirId) {
        return tugasAkhirRepository.getTugasAkhirByUserId(tugasAkhirId);
    }
}
