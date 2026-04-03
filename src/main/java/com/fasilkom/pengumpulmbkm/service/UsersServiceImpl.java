package com.fasilkom.pengumpulmbkm.service;



import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {


    private final UsersRepository usersRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Users findByUserId(String userId) {

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
    public void updateUsersPassword(String password, String userId) {
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
