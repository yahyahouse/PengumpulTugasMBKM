package com.fasilkom.pengumpulmbkm.repository;



import com.fasilkom.pengumpulmbkm.model.Enum.EProdi;
import com.fasilkom.pengumpulmbkm.model.Roles.Prodi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdiRepository extends JpaRepository<Prodi, Integer> {

    Optional<Prodi> findByName (EProdi roleName);

}

