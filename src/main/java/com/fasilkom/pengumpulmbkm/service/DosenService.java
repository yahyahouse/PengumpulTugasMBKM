package com.fasilkom.pengumpulmbkm.service;


import com.fasilkom.pengumpulmbkm.model.users.Dosen;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DosenService {

    void saveDosen(Dosen dosen);

    Dosen getDosenByDosenId(Integer dosenId);

    Dosen getDosenByUserId(Integer userId);

    void deletDosenByDosenId(Integer dosenId);

    List<Dosen> getAllDosen();

    boolean existsDosenByDosenId(Integer dosenId);

    boolean existsDosenByUserId(Integer userId);
}
