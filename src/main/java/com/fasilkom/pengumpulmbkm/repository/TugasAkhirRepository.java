package com.fasilkom.pengumpulmbkm.repository;



import com.fasilkom.pengumpulmbkm.model.roles.Prodi;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface TugasAkhirRepository extends JpaRepository<TugasAkhir, Integer> {

    TugasAkhir findByTugasAkhirId(Integer TugasAkhirId);

}

