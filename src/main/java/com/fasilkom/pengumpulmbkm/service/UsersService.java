package com.fasilkom.pengumpulmbkm.service;



import com.fasilkom.pengumpulmbkm.model.users.Users;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UsersService {

    Users findByUserId(String userId);

    Users findByUsername(String username);

    Users findByEmail(String email);

    void updateUsersPassword(String password, String userId);

    void updateProfile(Users users);

    List<Users> getAllUsers();

    void savePassword(Users users);
}
