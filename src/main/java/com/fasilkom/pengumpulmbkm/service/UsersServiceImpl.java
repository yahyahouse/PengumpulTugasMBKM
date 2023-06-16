package com.fasilkom.pengumpulmbkm.service;


import com.fasilkom.skripsi.model.Users;
import com.fasilkom.skripsi.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    UsersRepository usersRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Users findByUserId(Integer userId) {

        return usersRepository.findByUserId(userId);
    }

    @Override
    public Users findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    @Override
    public void updateUsersPassword(String password, Integer userId) {
        usersRepository.updatePassword(passwordEncoder.encode(password), userId);
    }
}
