package com.fasilkom.pengumpulmbkm.service;


import com.fasilkom.pengumpulmbkm.model.users.Users;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import java.util.List;

@Service
public interface UsersService {

    Users findByUserId (Integer userId);
    Users findByUsername (String username);
    void updateUsersPassword(String password, Integer userId);
    void updateProfile(Users users);

    List<Users> getAllUsers();
}
