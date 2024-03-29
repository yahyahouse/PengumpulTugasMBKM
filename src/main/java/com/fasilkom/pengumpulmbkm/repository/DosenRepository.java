package com.fasilkom.pengumpulmbkm.repository;


import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;


@Repository
@Transactional
public interface DosenRepository extends JpaRepository<Dosen,Integer> {


    @Query(value = "SELECT d.dosen_id, u.user_id, u.nama_lengkap, u.email, u.no_hp " +
            "from dosen d join users u on u.user_id=d.user_id where d.dosen_id=:dosen_id", nativeQuery = true)
    Dosen findDosenByDosenId(@Param("dosen_id") Integer dosenId);
    @Query(value = "SELECT d.dosen_id, u.user_id, u.nama_lengkap, u.email, u.no_hp " +
            "from dosen d join users u on u.user_id=d.user_id where d.user_id=:user_id", nativeQuery = true)
    Dosen findDosenByUserId(@Param("user_id") Integer userId);

    @Query(value = "SELECT d.dosen_id, u.user_id, u.nama_lengkap " +
            "FROM dosen d join users u on u.user_id=d.user_id", nativeQuery = true)
    List<Dosen> getAllDosen ();

    @Modifying
    @Query(value = "DELETE FROM dosen WHERE dosen_id=:dosen_id",nativeQuery = true)
    void deleteDosenByDosenId(
            @Param("dosen_id") Integer dosenId);
    boolean existsDosenByDosenId(Integer dosenId);
    boolean existsDosenByUserId(Integer userId);
}
