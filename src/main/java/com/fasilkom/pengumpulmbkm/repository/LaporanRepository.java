package com.fasilkom.pengumpulmbkm.repository;



import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface LaporanRepository extends JpaRepository<Laporan, Integer> {

    Laporan findByLaporanId(Integer LaporanId);

}

