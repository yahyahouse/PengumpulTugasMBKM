package com.fasilkom.pengumpulmbkm.controller;


import com.fasilkom.pengumpulmbkm.config.JwtUtils;
import com.fasilkom.pengumpulmbkm.model.*;
import com.fasilkom.pengumpulmbkm.model.Enum.ERole;
import com.fasilkom.pengumpulmbkm.model.User.Users;
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
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(
            @Schema (example = "{" +
                    "\"email\":\"userTest@gmail.com\"," +
                    "\"password\":\"userTest\"" +
                    "}")
            @RequestBody Map<String, Object> login) {

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
                    "\"password\":\"userTest\"," +
                    "\"role\":[\"DOSEN\", \"MAHASISWA\", \"ADMIN\"]" +
                    "}")
            @RequestBody SignupRequest signupRequest) {
        Boolean usernameExist = usersRepository.existsByUsername(signupRequest.getUsername());
        if(Boolean.TRUE.equals(usernameExist)) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        Boolean emailExist = usersRepository.existsByEmail(signupRequest.getEmail());
        if(Boolean.TRUE.equals(emailExist)) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already taken!"));
        }

        Users users = new Users(signupRequest.getUsername(), signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRole();
        Set<Roles> roles = new HashSet<>();

        if(strRoles == null) {
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
        users.setRoles(roles);
        usersRepository.save(users);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));

    }
    @PostMapping("/user/update-users-password/{userId}")
    public ResponseEntity<ResponseEntity> updateUsersPassword(
            @PathVariable("userId") Integer userId,
            @RequestParam("old_password") String oldPassword,
            @RequestParam("password") String password,
            @RequestParam("retype_password") String retypePassword,
            Authentication authentication) {
        Users user = usersService.findByUsername(authentication.getName());
        user.setUserId(userId);
        Users users = usersRepository.findByUserId(userId);
        if (password.equals(retypePassword)) {
            if (passwordEncoder.matches(oldPassword, users.getPassword())) {
                usersService.updateUsersPassword(password, userId);
                return new ResponseEntity(PASSWORD_TERGANTI, HttpStatus.OK);
            } else
                return new ResponseEntity(SALAH_PASSWORD, HttpStatus.BAD_REQUEST);

        } else
            return new ResponseEntity(PASSWORD_SAMA, HttpStatus.BAD_REQUEST);
    }
}