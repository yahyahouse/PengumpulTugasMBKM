package com.fasilkom.pengumpulmbkm.repository;



import com.fasilkom.pengumpulmbkm.model.roles.Prodi;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface TugasAkhirRepository extends JpaRepository<TugasAkhir, Integer> {

    TugasAkhir findByTugasAkhirId(Integer TugasAkhirId);

    @Query(value = "SELECT * FROM tugas_akhir t WHERE t.user_id =:userId", nativeQuery = true)
    List<TugasAkhir> getTugasAkhirByUserId (Integer userId);

    @Query(value = "SELECT * FROM tugas_akhir t WHERE t.dosen_id =:dosenId", nativeQuery = true)
    List<TugasAkhir> getTugasAkhirByDosenId (Integer dosenId);

    @Query(value = "SELECT * FROM tugas_akhir", nativeQuery = true)
    List<TugasAkhir> getAllTugasAkhir();

}

