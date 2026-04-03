package com.fasilkom.pengumpulmbkm.service;


import com.fasilkom.pengumpulmbkm.model.users.Dosen;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DosenService {

    void saveDosen(Dosen dosen);

    Dosen getDosenByDosenId(String dosenId);

    Dosen getDosenByUserId(String userId);

    void deletDosenByDosenId(String dosenId);

    List<Dosen> getAllDosen();

    boolean existsDosenByDosenId(String dosenId);

    boolean existsDosenByUserId(String userId);
}
