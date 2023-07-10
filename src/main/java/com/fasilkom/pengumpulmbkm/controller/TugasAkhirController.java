package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.response.TugasAkhirGetDetailResponse;
import com.fasilkom.pengumpulmbkm.model.response.TugasAkhirResponse;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.TugasAkhirService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fasilkom.pengumpulmbkm.model.Info.AKSES_DITOLAK;

@Tag(name = "Tugas Akhir MBKM", description = "API for processing various operations with Tugas Akhir entity")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/mahasiswa/tugas-akhir")
public class TugasAkhirController {

    @Autowired
    private UsersService usersService;
    @Autowired
    private DosenService dosenService;

    @Autowired
    private TugasAkhirService tugasAkhirService;

    @Operation(summary = "Upload Tugas Akhir")
    @PostMapping("/upload-tugas-akhir")
    public ResponseEntity<TugasAkhirResponse> uploadTugasAkhir(
            @RequestParam("dosenId") Integer dosenId,
            @RequestParam("sertifikat") MultipartFile sertifikat,
            @RequestParam("lembarPengesahan") MultipartFile lembarPengesahan,
            @RequestParam("nilai") MultipartFile nilai,
            @RequestParam("laporanTugasAkhir") MultipartFile laporanTugasAkhir,
            Authentication authentication) {
        try {
            Users user = usersService.findByUsername(authentication.getName());
            TugasAkhir TA = new TugasAkhir();
            Users users = usersService.findByUserId(user.getUserId());
            Dosen dosen = dosenService.getDosenByDosenId(dosenId);
            LocalDateTime currentTime = LocalDateTime.now();
            TA.setUserId(users);
            TA.setDosenId(dosen);
            TA.setSertifikat(sertifikat.getBytes());
            TA.setLembarPengesahan(lembarPengesahan.getBytes());
            TA.setNilai(nilai.getBytes());
            TA.setLaporanTugasAkhir(laporanTugasAkhir.getBytes());
            TA.setVerifikasi(null);
            TA.setWaktuPengumpulan(Timestamp.valueOf(currentTime));
            tugasAkhirService.saveTugasAkhir(TA);


            return new ResponseEntity(new TugasAkhirResponse(TA), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Update Laporan Tugas Akhir")
    @PutMapping("/update-tugas-akhir/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirResponse> updateTugasAkhir(
            @PathVariable("tugasAkhirId") Integer tugasAkhirId,
            @RequestParam("sertifikat") MultipartFile sertifikat,
            @RequestParam("lembarPengesahan") MultipartFile lembarPengesahan,
            @RequestParam("nilai") MultipartFile nilai,
            @RequestParam("laporanTugasAkhir") MultipartFile laporanTugasAkhir,
            Authentication authentication
    ) {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            TugasAkhir TA = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
            Users users = usersService.findByUsername(authentication.getName());
            if (TA.getUserId().getUserId().equals(users.getUserId())) {
                if (!sertifikat.isEmpty()) {
                    TA.setSertifikat(sertifikat.getBytes());
                }
                if (!lembarPengesahan.isEmpty()) {
                    TA.setLembarPengesahan(lembarPengesahan.getBytes());
                }
                if (!nilai.isEmpty()) {
                    TA.setNilai(nilai.getBytes());
                }
                if (!laporanTugasAkhir.isEmpty()) {
                    TA.setLaporanTugasAkhir(laporanTugasAkhir.getBytes());
                }
                TA.setWaktuUpdate(Timestamp.valueOf(currentTime));
                tugasAkhirService.saveTugasAkhir(TA);
                return new ResponseEntity(new TugasAkhirResponse(TA), HttpStatus.OK);
            } else {
                return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @Operation(summary = "menampilkan detail Laporan Tugas Akhir berdasarkan tugasAkhirId")
    @GetMapping("/detail-tugas-akhir/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirGetDetailResponse> getTugasAkhirByid(
            @PathVariable("tugasAkhirId") Integer tugasAkhirId,
            Authentication authentication) {
        TugasAkhir TA = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        if (TA.getUserId().getUserId().equals(users.getUserId())) {
            return new ResponseEntity<>(new TugasAkhirGetDetailResponse(TA), HttpStatus.OK);
        } else
            return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
    }

    @Operation(summary = "menampilkan daftar laporan sesuai season login ")
    @GetMapping("/list-tugas-akhir")
    public ResponseEntity<TugasAkhirResponse> getTugasAkhirByUserId(
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        List<TugasAkhir> TA = tugasAkhirService.getTugasAkhirByUserId(users.getUserId());
        List<TugasAkhirResponse> TAGetResponse =
                TA.stream().map(TugasAkhirResponse::new).collect(Collectors.toList());

        return new ResponseEntity(TAGetResponse, HttpStatus.OK);
    }

    @Operation(summary = "menampilkan file sertifikat")
    @GetMapping("/sertifikat/{tugasAkhirId}")
    public ResponseEntity<Resource> displayFileSertifikat(
            @PathVariable Integer tugasAkhirId,
            Authentication authentication) {
        TugasAkhir TA = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        if (TA.getUserId().getUserId().equals(users.getUserId())) {
            Optional<TugasAkhir> optionalDocument = Optional.ofNullable(tugasAkhirService.findByTugasAkhirId(tugasAkhirId));
            if (optionalDocument.isPresent()) {
                TugasAkhir tugasAkhir = optionalDocument.get();
                byte[] fileBytes = tugasAkhir.getSertifikat();

                ByteArrayResource resource = new ByteArrayResource(fileBytes);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sertifikat.pdf");

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(fileBytes.length)
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
        }

    }

    @Operation(summary = "menampilkan file nilai")
    @GetMapping("/nilai/{tugasAkhirId}")
    public ResponseEntity<Resource> displayFileNilai(
            @PathVariable Integer tugasAkhirId,
            Authentication authentication) {
        TugasAkhir TA = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        if (TA.getUserId().getUserId().equals(users.getUserId())) {
            Optional<TugasAkhir> optionalDocument = Optional.ofNullable(tugasAkhirService.findByTugasAkhirId(tugasAkhirId));
            if (optionalDocument.isPresent()) {
                TugasAkhir tugasAkhir = optionalDocument.get();
                byte[] fileBytes = tugasAkhir.getNilai();

                ByteArrayResource resource = new ByteArrayResource(fileBytes);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=nilai.pdf");

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(fileBytes.length)
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
        }

    }

    @Operation(summary = "menampilkan file Lembar Pengesahan")
    @GetMapping("/lembar-pengesahan/{tugasAkhirId}")
    public ResponseEntity<Resource> displayFileLembarPengesahan(
            @PathVariable Integer tugasAkhirId,
            Authentication authentication) {
        TugasAkhir TA = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        if (TA.getUserId().getUserId().equals(users.getUserId())) {
            Optional<TugasAkhir> optionalDocument = Optional.ofNullable(tugasAkhirService.findByTugasAkhirId(tugasAkhirId));
            if (optionalDocument.isPresent()) {
                TugasAkhir tugasAkhir = optionalDocument.get();
                byte[] fileBytes = tugasAkhir.getLembarPengesahan();

                ByteArrayResource resource = new ByteArrayResource(fileBytes);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=lembarPengesahan.pdf");

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(fileBytes.length)
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
        }

    }

    @Operation(summary = "menampilkan file Laporan Tugas Akhir")
    @GetMapping("/laporan-tugas-akhir/{tugasAkhirId}")
    public ResponseEntity<Resource> displayFileTugasAkhir(
            @PathVariable Integer tugasAkhirId,
            Authentication authentication) {
        TugasAkhir TA = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        if (TA.getUserId().getUserId().equals(users.getUserId())) {
            Optional<TugasAkhir> optionalDocument = Optional.ofNullable(tugasAkhirService.findByTugasAkhirId(tugasAkhirId));
            if (optionalDocument.isPresent()) {
                TugasAkhir tugasAkhir = optionalDocument.get();
                byte[] fileBytes = tugasAkhir.getLaporanTugasAkhir();

                ByteArrayResource resource = new ByteArrayResource(fileBytes);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=TugasAkhir.pdf");

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(fileBytes.length)
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
        }


    }
}


