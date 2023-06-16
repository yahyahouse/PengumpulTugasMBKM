package com.fasilkom.pengumpulmbkm.service;


import com.fasilkom.skripsi.model.UserDetailsImpl;
import com.fasilkom.skripsi.model.Users;
import com.fasilkom.skripsi.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(username);
        return UserDetailsImpl.build(user);
    }
}
