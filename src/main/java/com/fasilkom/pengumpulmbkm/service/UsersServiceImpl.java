package com.fasilkom.pengumpulmbkm.service;



import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Users findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    @Override
    public void updateUsersPassword(String password, Integer userId) {
        usersRepository.updatePassword(passwordEncoder.encode(password), userId);
    }

    @Override
    public void updateProfile(Users users) {
        usersRepository.save(users);
    }

    @Override
    public List<Users> getAllUsers() {
        return usersRepository.getAllUsers();
    }

    @Override
    public void savePassword(Users users) {
        usersRepository.save(users);
    }



}
