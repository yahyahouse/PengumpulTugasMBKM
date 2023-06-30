package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.repository.DosenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DosenServiceImpl implements DosenService{

    @Autowired
    private DosenRepository dosenRepository;
    @Override
    public void saveDosen(Dosen dosen) {
        dosenRepository.save(dosen);
    }

    @Override
    public Dosen getDosenByUserId(Integer userId) {
        return dosenRepository.findDosenByUserId(userId);
    }

    @Override
    public Dosen getDosenByDosenId(Integer dosenId) {
        return dosenRepository.findDosenByDosenId(dosenId);
    }

    @Override
    public void deletDosenByDosenId(Integer dosenId) {
        dosenRepository.deleteDosenByDosenId(dosenId);
    }

    @Override
    public List<Dosen> getAllDosen() {
        return dosenRepository.getAllDosen();
    }

    @Override
    public boolean existsDosenByDosenId(Integer dosenId) {
        return dosenRepository.existsDosenByDosenId(dosenId);
    }

    @Override
    public boolean existsDosenByUserId(Integer userId) {
        return dosenRepository.existsDosenByUserId(userId);
    }
}
