package com.fasilkom.pengumpulmbkm.repository;



import com.fasilkom.pengumpulmbkm.model.Enum.EProgram;
import com.fasilkom.pengumpulmbkm.model.Roles.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Integer> {

    Optional<Program> findByName (EProgram roleName);

}

