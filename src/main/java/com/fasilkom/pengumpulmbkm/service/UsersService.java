package com.fasilkom.pengumpulmbkm.service;


import com.fasilkom.pengumpulmbkm.model.users.Users;
import org.springframework.stereotype.Service;

@Service
public interface UsersService {

    Users findByUserId (Integer userId);
    Users findByUsername (String username);
    public void updateUsersPassword(String password, Integer userId);
}
