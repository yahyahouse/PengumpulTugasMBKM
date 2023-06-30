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
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Dosen", description = "API for processing various operations with Dosen entity")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/dosen")
public class DosenController {
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
        List<Dosen> dosen =dosenService.getAllDosen();
        List<DosenResponse> allDosen =
                dosen.stream().map(DosenResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(allDosen, HttpStatus.OK);
    }

    @Operation(summary = "Verifikasi Laporan MBKM menjadi diterima")
    @PostMapping("/verifikasi-laporan/{laporanId}")
    public ResponseEntity<LaporanResponse> verifikasiLaporanTrue(
            @PathVariable("laporanId") Integer laporanId) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        LocalDateTime currentTime = LocalDateTime.now();
        laporan.setVerifikasi(true);
        laporan.setWaktuUpdate(Timestamp.valueOf(currentTime));
        laporanService.saveLaporan(laporan);

        return new ResponseEntity(new LaporanResponse(laporan), HttpStatus.OK);
    }
    @Operation(summary = "Verifikasi Laporan MBKM menjadi ditolak")
    @PostMapping("/verifikasi-laporan/{laporanId}")
    public ResponseEntity<LaporanResponse> verifikasiLaporanFalse(
            @PathVariable("laporanId") Integer laporanId) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        LocalDateTime currentTime = LocalDateTime.now();
        laporan.setVerifikasi(false);
        laporan.setWaktuUpdate(Timestamp.valueOf(currentTime));
        laporanService.saveLaporan(laporan);

        return new ResponseEntity(new LaporanResponse(laporan), HttpStatus.OK);
    }

    @Operation(summary = "Verifikasi Tugas Akhir MBKM menjadi di terima")
    @PostMapping("/verifikasi-tugas-akhir/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirResponse> verifikasiTugasAkhirTrue(
            @PathVariable("tugasAkhirId") Integer tugasAkhirId) {
        TugasAkhir TA = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        LocalDateTime currentTime = LocalDateTime.now();
        TA.setVerifikasi(true);
        TA.setWaktuUpdate(Timestamp.valueOf(currentTime));
        tugasAkhirService.saveTugasAkhir(TA);

        return new ResponseEntity(new TugasAkhirResponse(TA), HttpStatus.OK);
    }

    @Operation(summary = "Verifikasi Tugas Akhir MBKM menjadi ditolak")
    @PostMapping("/verifikasi-tugas-akhir/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirResponse> verifikasiTugasAkhirFalse(
            @PathVariable("tugasAkhirId") Integer tugasAkhirId) {
        TugasAkhir TA = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        LocalDateTime currentTime = LocalDateTime.now();
        TA.setVerifikasi(false);
        TA.setWaktuUpdate(Timestamp.valueOf(currentTime));
        tugasAkhirService.saveTugasAkhir(TA);

        return new ResponseEntity(new TugasAkhirResponse(TA), HttpStatus.OK);
    }

    @Operation(summary = "menampilkan daftar Laporan berdasarkan userId dosen ")
    @GetMapping("/list-laporan")
    public ResponseEntity<LaporanResponse> getLaporanByUserId(
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        List<Laporan> laporan = laporanService.findLaporanByDosenId(dosen.getDosenId());
        List<LaporanResponse> TAGetResponse =
                laporan.stream().map(LaporanResponse::new).collect(Collectors.toList());

        return new ResponseEntity(TAGetResponse, HttpStatus.OK);
    }

    @GetMapping("/list-tugas-akhir")
    public ResponseEntity<TugasAkhirResponse> getTugasAkhirByUserId(
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        List<TugasAkhir> TA = tugasAkhirService.getTugasAkhirByDosenId(dosen.getDosenId());
        List<TugasAkhirResponse> TAGetResponse =
                TA.stream().map(TugasAkhirResponse::new).collect(Collectors.toList());

        return new ResponseEntity(TAGetResponse, HttpStatus.OK);
    }

}
