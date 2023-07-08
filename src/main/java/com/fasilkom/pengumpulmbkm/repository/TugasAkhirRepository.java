package com.fasilkom.pengumpulmbkm.repository;



import com.fasilkom.pengumpulmbkm.model.roles.Prodi;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
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

    @Modifying
    @Query(value = "UPDATE tugas_akhir SET laporan_tugas_akhir=:tugas_akhir, lembar_pengesahan=:lembar_pengesahan," +
            " nilai=:nilai, sertifikat=:sertifikat, waktu_update=:waktu_update  " +
            "WHERE tugas_akhir_id=:tugas_akhir_id",nativeQuery = true)
    void updateTugasAkhirsById(@Param("tugas_akhir_id")Integer tugasAkhirId,
                               @Param("tugas_akhir")byte[] tugasAkhir,
                               @Param("lembar_pengesahan")byte[] lembarPengesahan,
                               @Param("nilai")byte[] nilai,
                               @Param("sertifikat")byte[] sertifikat,
                               @Param("waktu_update")Timestamp waktuUpdate);

    @Modifying
    @Query(value = "UPDATE tugas_akhir SET catatan=:catatan, waktu_update=:waktu_update " +
            "WHERE tugas_akhir_id=:tugas_akhir_id",nativeQuery = true)
    void updateTugasAkhirsForCatatanAndVerifikasiById(
            @Param("tugas_akhir_id")Integer tugasAkhirId,
            @Param("catatan")String catatan,
            @Param("waktu_update")Timestamp waktuUpdate);

}

