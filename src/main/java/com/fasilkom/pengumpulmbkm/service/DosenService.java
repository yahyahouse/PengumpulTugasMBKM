package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.repository.DosenRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
