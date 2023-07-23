package com.fasilkom.pengumpulmbkm.repository;



import com.fasilkom.pengumpulmbkm.model.enumeration.EProgram;
import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Integer> {

    Optional<Program> findByName (EProgram roleName);
    @Query(value = "SELECT * FROM program", nativeQuery = true)
    List<Program> getAllProgram();

    Program findByProgramId(Integer programId);

}

