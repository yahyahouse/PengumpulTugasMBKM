package com.fasilkom.pengumpulmbkm.repository;

import com.fasilkom.pengumpulmbkm.model.AccountRecoveryToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface AccountRecoveryTokenRepository extends JpaRepository<AccountRecoveryToken, Long> {
    AccountRecoveryToken findByToken(String token);
}
