package com.fasilkom.pengumpulmbkm.repository;



import com.fasilkom.skripsi.model.ERole;
import com.fasilkom.skripsi.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Integer> {

    Optional<Roles> findByName (ERole roleName);
}

