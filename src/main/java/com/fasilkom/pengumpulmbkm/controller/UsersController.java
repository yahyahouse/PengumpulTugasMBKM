package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.response.DosenResponse;
import com.fasilkom.pengumpulmbkm.model.response.ProfileResponse;
import com.fasilkom.pengumpulmbkm.model.response.UsersResponse;
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
    @Autowired
    private DosenService dosenService;

    @Operation(summary = "Get detail profile")
    @GetMapping(value = "/profil")
    public ResponseEntity<UsersResponse> getDetailUser(
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        return new ResponseEntity<>(new UsersResponse(users), HttpStatus.OK);
    }

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

    @Operation(summary = "melakukan update profile ")
    @PostMapping("/update-users-profile")
    public ResponseEntity updateUsersProfile(
            @RequestParam("noHp") String noHp,
            @RequestParam("namaLengkap") String namalengkap,
            @RequestParam("npm") String npm,
            @RequestParam("password") String password,
            Authentication authentication) {
        Users user = usersService.findByUsername(authentication.getName());
        Users users = usersService.findByUserId(user.getUserId());
        if (passwordEncoder.matches(password, users.getPassword())) {
            users.setNamaLengkap(namalengkap);
            users.setNoHp(noHp);
            users.setNpm(npm);
            usersService.updateProfile(users);
            return new ResponseEntity(UPDATE_BERHASIL, HttpStatus.OK);
        } else
            return new ResponseEntity(SALAH_PASSWORD, HttpStatus.BAD_REQUEST);

    }

    @Operation(summary = "Get all dosen")
    @GetMapping(value = "/all-dosen")
    public ResponseEntity<List<DosenResponse>> getAllDosen() {
        List<Dosen> dosen = dosenService.getAllDosen();
        List<DosenResponse> allDosen =
                dosen.stream().map(DosenResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(allDosen, HttpStatus.OK);
    }

    @Operation(summary = "Get detail dosen")
    @GetMapping(value = "/detail-profil-dosen/{dosenId}")
    public ResponseEntity<DosenResponse> getDetailDosenByDosenId(@PathVariable("dosenId") Integer dosenId) {
        Dosen dosen = dosenService.getDosenByDosenId(dosenId);
        if (dosen.getDosenId() == null){
            return new ResponseEntity("Not Found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new DosenResponse(dosen), HttpStatus.OK);
    }
}
