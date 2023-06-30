package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.response.DosenResponse;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.LaporanService;
import com.fasilkom.pengumpulmbkm.service.TugasAkhirService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.fasilkom.pengumpulmbkm.model.Info.*;

@Tag(name = "Users", description = "API for processing various operations with User entity")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/users")
public class UsersController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private UsersService usersService;

    @Operation(summary = "melakukan update password ")
    @PostMapping("/update-users-password")
    public ResponseEntity<ResponseEntity> updateUsersPassword(
            @RequestParam("old_password") String oldPassword,
            @RequestParam("password") String password,
            @RequestParam("retype_password") String retypePassword,
            Authentication authentication) {
        Users user = usersService.findByUsername(authentication.getName());
        Users users = usersService.findByUserId(user.getUserId());
        if (password.equals(retypePassword)) {
            if (passwordEncoder.matches(oldPassword, users.getPassword())) {
                usersService.updateUsersPassword(password, user.getUserId());
                return new ResponseEntity(PASSWORD_TERGANTI, HttpStatus.OK);
            } else
                return new ResponseEntity(SALAH_PASSWORD, HttpStatus.BAD_REQUEST);

        } else
            return new ResponseEntity(PASSWORD_SAMA, HttpStatus.BAD_REQUEST);
    }

}
