package com.fasilkom.pengumpulmbkm.controller;


import com.fasilkom.pengumpulmbkm.config.JwtUtils;
import com.fasilkom.pengumpulmbkm.model.*;
import com.fasilkom.pengumpulmbkm.model.enumeration.EProdi;
import com.fasilkom.pengumpulmbkm.model.enumeration.ERole;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.model.roles.Roles;
import com.fasilkom.pengumpulmbkm.model.roles.Prodi;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.repository.ProdiRepository;
import com.fasilkom.pengumpulmbkm.repository.ProgramRepository;
import com.fasilkom.pengumpulmbkm.repository.RoleRepository;
import com.fasilkom.pengumpulmbkm.repository.UsersRepository;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

@Tag(name = "Auth", description = "API untuk memproses berbagai operasi untuk dapat mengakses sistem")
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

    @Operation(summary = "Login untuk dapat mengakses sisetem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(
            @Schema(example = "{" +
                    "\"email\":\"userTest@gmail.com\"," +
                    "\"password\":\"userTest\"" +
                    "}")
            @RequestBody Map<String, Object> login) {
        try {
        Users users = usersRepository.findUsersByEmail(login.get("email").toString());
        if (users == null) {
            MessageResponse messageResponse = new MessageResponse("Account Not Found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageResponse);
        }



            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(users.getUsername(), login.get("password")));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            JwtResponse jwtResponse = new JwtResponse(jwt,
                    userDetails.getUserId(), userDetails.getUsername(), userDetails.getEmail(),
                    roles);

            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            MessageResponse messageResponse = new MessageResponse("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageResponse);
        }
    }


    @Operation(summary = "Registrasi akun baru")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(
            @Valid
            @Schema(example = "{" +
                    "\"username\":\"userTest\"," +
                    "\"email\":\"userTest@gmail.com\"," +
                    "\"namaLengkap\":\"userTestLengkap\"," +
                    "\"password\":\"userTest\"," +
                    "\"npm\":\"1234567891011\"," +
                    "\"role\":[\"DOSEN\", \"MAHASISWA\", \"ADMIN\"]," +
                    "\"prodi\":[\"TI\"]" +
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
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));

    }

}