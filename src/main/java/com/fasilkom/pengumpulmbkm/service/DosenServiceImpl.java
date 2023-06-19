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
    public List<Dosen> getDosenByUserId(Integer dosenId) {
        return dosenRepository.findDosenByDosenId(dosenId);
    }
}
