package com.fasilkom.pengumpulmbkm.repository;



import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface LaporanRepository extends JpaRepository<Laporan, Integer> {

    Laporan findByLaporanId(Integer laporanId);

    @Query(value = "SELECT * FROM laporan l WHERE l.user_id =:userId", nativeQuery = true)
    List<Laporan> getLaporanByUserId (Integer userId);

    @Query(value = "SELECT * FROM laporan l WHERE l.dosen_id =:dosenId", nativeQuery = true)
    List<Laporan> getLaporanByDosenId (Integer dosenId);

    @Query(value = "SELECT * FROM laporan", nativeQuery = true)
    List<Laporan> getAllLaporan();

}

