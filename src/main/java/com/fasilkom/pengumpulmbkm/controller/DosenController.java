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

import static com.fasilkom.pengumpulmbkm.model.Info.AKSES_DITOLAK;

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



    @Operation(summary = "Verifikasi Laporan MBKM menjadi diterima")
    @PostMapping("/verifikasi-laporan-true/{laporanId}")
    public ResponseEntity<LaporanResponse> verifikasiLaporanTrue(
            @PathVariable("laporanId") Integer laporanId,
            Authentication authentication) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (laporan.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            LocalDateTime currentTime = LocalDateTime.now();
            laporan.setVerifikasi(true);
            laporan.setWaktuUpdate(Timestamp.valueOf(currentTime));
            laporanService.saveLaporan(laporan);
            return new ResponseEntity(new LaporanResponse(laporan), HttpStatus.OK);
        } else
            return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);

    }

    @Operation(summary = "Verifikasi Laporan MBKM menjadi ditolak")
    @PostMapping("/verifikasi-laporan-false/{laporanId}")
    public ResponseEntity<LaporanResponse> verifikasiLaporanFalse(
            @PathVariable("laporanId") Integer laporanId,
            @RequestParam("catatan") String catatan,
            Authentication authentication) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (laporan.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            LocalDateTime currentTime = LocalDateTime.now();
            laporan.setCatatan(catatan);
            laporan.setVerifikasi(false);
            laporan.setWaktuUpdate(Timestamp.valueOf(currentTime));
            laporanService.saveLaporan(laporan);

            return new ResponseEntity(new LaporanResponse(laporan), HttpStatus.OK);
        } else
            return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);


    }

    @Operation(summary = "Verifikasi Tugas Akhir MBKM menjadi di terima")
    @PostMapping("/verifikasi-tugas-akhir-true/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirResponse> verifikasiTugasAkhirTrue(
            @PathVariable("tugasAkhirId") Integer tugasAkhirId,
            Authentication authentication) {
        TugasAkhir TA = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (TA.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            LocalDateTime currentTime = LocalDateTime.now();
            TA.setVerifikasi(true);
            TA.setWaktuUpdate(Timestamp.valueOf(currentTime));
            tugasAkhirService.saveTugasAkhir(TA);

            return new ResponseEntity(new TugasAkhirResponse(TA), HttpStatus.OK);
        } else
            return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);


    }

    @Operation(summary = "Verifikasi Tugas Akhir MBKM menjadi ditolak")
    @PostMapping("/verifikasi-tugas-akhir-false/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirResponse> verifikasiTugasAkhirFalse(
            @PathVariable("tugasAkhirId") Integer tugasAkhirId,
            @RequestParam("catatan") String catatan,
            Authentication authentication) {
        TugasAkhir TA = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (TA.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            LocalDateTime currentTime = LocalDateTime.now();
            TA.setCatatan(catatan);
            TA.setVerifikasi(false);
            TA.setWaktuUpdate(Timestamp.valueOf(currentTime));
            tugasAkhirService.saveTugasAkhir(TA);

            return new ResponseEntity(new TugasAkhirResponse(TA), HttpStatus.OK);
        } else
            return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);


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

    @Operation(summary = "menampilkan daftar Tugas AKhir berdasarkan userId dosen ")
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

    @Operation(summary = "menampilkan detail Laporan Tugas Akhir berdasarkan tugasAkhirId")
    @GetMapping("/detail-tugas-akhir/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirResponse> getDetailTugasAkhirById(
            @PathVariable("tugasAkhirId") Integer tugasAkhirId,
            Authentication authentication) {
        TugasAkhir TA = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (TA.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            return new ResponseEntity<>(new TugasAkhirResponse(TA), HttpStatus.OK);
        } else
            return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
    }

    @Operation(summary = "menampilkan detail Laporan berdasarkan laporanId")
    @GetMapping("/detail-laporan/{laporanId}")
    public ResponseEntity<LaporanResponse> getDetailLaporanById(
            @PathVariable("laporanId") Integer laporanId,
            Authentication authentication) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (laporan.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            return new ResponseEntity<>(new LaporanResponse(laporan), HttpStatus.OK);
        } else
            return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
    }

}
