package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.repository.DosenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DosenServiceImpl implements DosenService {

    private final DosenRepository dosenRepository;

    @Override
    public void saveDosen(Dosen dosen) {
        dosenRepository.save(dosen);
    }

    @Override
    public Dosen getDosenByUserId(String userId) {
        return dosenRepository.findDosenByUserId(userId);
    }

    @Override
    public Dosen getDosenByDosenId(String dosenId) {
        return dosenRepository.findDosenByDosenId(dosenId);
    }

    @Override
    public void deletDosenByDosenId(String dosenId) {
        dosenRepository.deleteDosenByDosenId(dosenId);
    }

    @Override
    public List<Dosen> getAllDosen() {
        return dosenRepository.getAllDosen();
    }

    @Override
    public boolean existsDosenByDosenId(String dosenId) {
        return dosenRepository.existsDosenByDosenId(dosenId);
    }

    @Override
    public boolean existsDosenByUserId(String userId) {
        return dosenRepository.existsDosenByUserId(userId);
    }
}
