package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.model.AccountRecoveryToken;
import com.fasilkom.pengumpulmbkm.model.users.Users;

import java.time.LocalDateTime;

public interface AccountRecoveryService {

    void createRecoveryToken(Users user);

    AccountRecoveryToken getRecoveryTokenByToken(String token);

    String generateToken();

    LocalDateTime calculateExpirationDate();

    void saveToken(AccountRecoveryToken accountRecoveryToken);

    boolean validateRecoveryToken(String token);

}
