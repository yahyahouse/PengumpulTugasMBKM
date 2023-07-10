package com.fasilkom.pengumpulmbkm.controller;


import com.fasilkom.pengumpulmbkm.config.JwtUtils;
import com.fasilkom.pengumpulmbkm.model.*;
import com.fasilkom.pengumpulmbkm.model.enumeration.EProdi;
import com.fasilkom.pengumpulmbkm.model.enumeration.EProgram;
import com.fasilkom.pengumpulmbkm.model.enumeration.ERole;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.model.roles.Roles;
import com.fasilkom.pengumpulmbkm.model.roles.Prodi;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.repository.ProdiRepository;
import com.fasilkom.pengumpulmbkm.repository.ProgramRepository;
import com.fasilkom.pengumpulmbkm.repository.RoleRepository;
import com.fasilkom.pengumpulmbkm.repository.UsersRepository;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fasilkom.pengumpulmbkm.model.Info.*;

@Tag(name = "Auth", description = "API for processing various operations with Auth entity")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersService usersService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ProdiRepository prodiRepository;

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(
            @Schema(example = "{" +
                    "\"email\":\"userTest@gmail.com\"," +
                    "\"password\":\"userTest\"" +
                    "}")
            @RequestBody Map<String, Object> login) {

        if (usersRepository.findUsersByEmail(login.get("email").toString()) == null) {
            return new ResponseEntity(new MessageResponse("Account Not Found"), HttpStatus.NOT_FOUND);
        }
        Users users = usersRepository.findUsersByEmail(login.get("email").toString());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(users.getUsername(), login.get("password")));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());


        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getUserId(), userDetails.getUsername(), userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(
            @Valid
            @Schema(example = "{" +
                    "\"username\":\"userTest\"," +
                    "\"email\":\"userTest@gmail.com\"," +
                    "\"namaLengakap\":\"userTestLengkap\"," +
                    "\"password\":\"userTest\"," +
                    "\"role\":[\"DOSEN\", \"MAHASISWA\", \"ADMIN\"]," +
                    "\"prodi\":[\"TI\"]," +
                    "\"program\":[\"BANGKIT\"]" +
                    "}")
            @RequestBody SignupRequest signupRequest) {
        Boolean usernameExist = usersRepository.existsByUsername(signupRequest.getUsername());
        if (Boolean.TRUE.equals(usernameExist)) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        Boolean emailExist = usersRepository.existsByEmail(signupRequest.getEmail());
        if (Boolean.TRUE.equals(emailExist)) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already taken!"));
        }

        Users users = new Users(signupRequest.getUsername(), signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()));

        //untuk roles
        Set<String> strRoles = signupRequest.getRole();
        Set<Roles> roles = new HashSet<>();

        if (strRoles == null) {
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

        if (strProdi == null) {
            Prodi prodi = prodiRepository.findByName(EProdi.TI)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            enumProdi.add(prodi);
        } else {
            strProdi.forEach(prodi -> {
                Prodi prodi1 = prodiRepository.findByName(EProdi.valueOf(prodi))
                        .orElseThrow(() -> new RuntimeException("Error: Role " + prodi + " is not found"));
                enumProdi.add(prodi1);
            });
        }

        //untuk program
        Set<String> strProgram = signupRequest.getProgram();
        Set<Program> enumProgram = new HashSet<>();

        if (strProgram == null) {
            Program program = programRepository.findByName(EProgram.BANGKIT)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            enumProgram.add(program);
        } else {
            strProgram.forEach(program -> {
                Program program1 = programRepository.findByName(EProgram.valueOf(program))
                        .orElseThrow(() -> new RuntimeException("Error: Role " + program + " is not found"));
                enumProgram.add(program1);
            });
        }
        users.setRoles(roles);
        users.setProgramStudi(enumProdi);
        users.setNamaLengkap(signupRequest.getNamaLengkap());
        users.setProgramMBKM(enumProgram);
        usersRepository.save(users);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));

    }

}