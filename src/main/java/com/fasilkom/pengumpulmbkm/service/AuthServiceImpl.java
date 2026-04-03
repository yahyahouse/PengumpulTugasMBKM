package com.fasilkom.pengumpulmbkm.service;

import com.fasilkom.pengumpulmbkm.config.JwtUtils;
import com.fasilkom.pengumpulmbkm.model.*;
import com.fasilkom.pengumpulmbkm.model.enumeration.EProdi;
import com.fasilkom.pengumpulmbkm.model.enumeration.ERole;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.model.roles.Prodi;
import com.fasilkom.pengumpulmbkm.model.roles.Roles;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.repository.ProdiRepository;
import com.fasilkom.pengumpulmbkm.repository.RoleRepository;
import com.fasilkom.pengumpulmbkm.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final ProdiRepository prodiRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public JwtResponse authenticateUser(Map<String, Object> login) {
        Users users = usersRepository.findUsersByEmail(login.get("email").toString());
        if (users == null) {
            throw new IllegalArgumentException("Account Not Found");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(users.getUsername(), login.get("password")));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new JwtResponse(jwt,
                userDetails.getUserId(), userDetails.getUsername(), userDetails.getEmail(),
                roles);
    }

    @Override
    public MessageResponse registerUser(SignupRequest signupRequest) {
        Boolean usernameExist = usersRepository.existsByUsername(signupRequest.getUsername());
        if (Boolean.TRUE.equals(usernameExist)) {
            throw new IllegalArgumentException("Error: Username is already taken!");
        }

        Boolean emailExist = usersRepository.existsByEmail(signupRequest.getEmail());
        if (Boolean.TRUE.equals(emailExist)) {
            throw new IllegalArgumentException("Error: Email is already taken!");
        }

        Users users = new Users(signupRequest.getUsername(), signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()));

        //untuk roles
        Set<String> strRoles = signupRequest.getRole();
        Set<Roles> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Roles role = roleRepository.findByName(ERole.MAHASISWA)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            roles.add(role);
        } else {
            strRoles.forEach(role -> {
                Roles roles1 = roleRepository.findByName(ERole.valueOf(role))
                        .orElseThrow(() -> new RuntimeException("Error: Role " + role + " is not found"));
                roles.add(roles1);
            });
        }

        //untuk prodi
        Set<String> strProdi = signupRequest.getProdi();
        Set<Prodi> enumProdi = new HashSet<>();

        if (strProdi == null || strProdi.isEmpty()) {
            Prodi prodi = prodiRepository.findByName(EProdi.TI)
                    .orElseThrow(() -> new RuntimeException("Error: Program Studi is not found"));
            enumProdi.add(prodi);
        } else {
            strProdi.forEach(prodi -> {
                Prodi prodi1 = prodiRepository.findByName(EProdi.valueOf(prodi))
                        .orElseThrow(() -> new RuntimeException("Error: Role " + prodi + " is not found"));
                enumProdi.add(prodi1);
            });
        }

        users.setRoles(roles);
        users.setProgramStudi(enumProdi);
        users.setNamaLengkap(signupRequest.getNamaLengkap());
        users.setNpm(signupRequest.getNpm());
        usersRepository.save(users);

        return new MessageResponse("User registered successfully");
    }
}
