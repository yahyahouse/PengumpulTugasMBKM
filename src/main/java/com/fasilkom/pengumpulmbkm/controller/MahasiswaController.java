package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.response.DosenResponse;
import com.fasilkom.pengumpulmbkm.model.response.LaporanResponse;
import com.fasilkom.pengumpulmbkm.model.response.TugasAkhirResponse;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasilkom.pengumpulmbkm.model.Info.*;

@Tag(name = "Mahasiswa", description = "API for processing various operations with Dosen entity")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/mahasiswa")
public class MahasiswaController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private DosenService dosenService;
    @Autowired
    private UsersService usersService;
    @Autowired
    private LaporanService laporanService;
    @Autowired
    private TugasAkhirService tugasAkhirService;



    @Operation(summary = "Get all dosen")
    @GetMapping(value = "/all-dosen")
    public ResponseEntity<List<DosenResponse>> getAllDosen() {
        List<Dosen> dosen = dosenService.getAllDosen();
        List<DosenResponse> allDosen =
                dosen.stream().map(DosenResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(allDosen, HttpStatus.OK);
    }

}
