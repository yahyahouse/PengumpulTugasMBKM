package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.model.response.TugasAkhirGetDetailResponse;
import com.fasilkom.pengumpulmbkm.model.response.TugasAkhirResponse;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.TugasAkhirService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
    @PostMapping(value = "/upload-tugas-akhir",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TugasAkhirResponse> uploadTugasAkhir(
            @RequestParam("dosenId") Integer dosenId,
            @RequestParam("sertifikat") MultipartFile sertifikat,
            @RequestParam("lembarPengesahan") MultipartFile lembarPengesahan,
            @RequestParam("nilai") MultipartFile nilai,
            @RequestParam("laporanTugasAkhir") MultipartFile laporanTugasAkhir,
            Authentication authentication) {
        try {
            Users user = usersService.findByUsername(authentication.getName());
            TugasAkhir ta = new TugasAkhir();
            Users users = usersService.findByUserId(user.getUserId());
            Dosen dosen = dosenService.getDosenByDosenId(dosenId);
            LocalDateTime currentTime = LocalDateTime.now();
            ta.setUserId(users);
            ta.setDosenId(dosen);
            ta.setSertifikat(sertifikat.getBytes());
            ta.setLembarPengesahan(lembarPengesahan.getBytes());
            ta.setNilai(nilai.getBytes());
            ta.setLaporanTugasAkhir(laporanTugasAkhir.getBytes());
            ta.setVerifikasi(null);
            ta.setWaktuPengumpulan(Timestamp.valueOf(currentTime));
            tugasAkhirService.saveTugasAkhir(ta);


            return new ResponseEntity<>(new TugasAkhirResponse(ta), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Update Laporan Tugas Akhir")
    @PutMapping(value = "/update-tugas-akhir/{tugasAkhirId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
            TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
            Users users = usersService.findByUsername(authentication.getName());
            if (ta.getUserId().getUserId().equals(users.getUserId())) {
                if (!sertifikat.isEmpty()) {
                    ta.setSertifikat(sertifikat.getBytes());
                }
                if (!lembarPengesahan.isEmpty()) {
                    ta.setLembarPengesahan(lembarPengesahan.getBytes());
                }
                if (!nilai.isEmpty()) {
                    ta.setNilai(nilai.getBytes());
                }
                if (!laporanTugasAkhir.isEmpty()) {
                    ta.setLaporanTugasAkhir(laporanTugasAkhir.getBytes());
                }
                ta.setWaktuUpdate(Timestamp.valueOf(currentTime));
                tugasAkhirService.saveTugasAkhir(ta);
                return new ResponseEntity<>(new TugasAkhirResponse(ta), HttpStatus.OK);
            } else {
                return new ResponseEntity(new MessageResponse(AKSES_DITOLAK), HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
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
        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        if (ta.getUserId().getUserId().equals(users.getUserId())) {
            return new ResponseEntity<>(new TugasAkhirGetDetailResponse(ta), HttpStatus.OK);
        } else
            return new ResponseEntity(new MessageResponse(AKSES_DITOLAK), HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
    }

    @Operation(summary = "menampilkan daftar laporan sesuai season login ")
    @GetMapping("/list-tugas-akhir")
    public ResponseEntity<TugasAkhirResponse> getTugasAkhirByUserId(
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        List<TugasAkhir> ta = tugasAkhirService.getTugasAkhirByUserId(users.getUserId());
        List<TugasAkhirResponse> taGetResponse =
                ta.stream().map(TugasAkhirResponse::new).collect(Collectors.toList());

        return new ResponseEntity(taGetResponse, HttpStatus.OK);
    }

    @Operation(summary = "menampilkan file sertifikat")
    @GetMapping("/sertifikat/{tugasAkhirId}")
    public ResponseEntity<Resource> displayFileSertifikat(
            @PathVariable Integer tugasAkhirId,
            Authentication authentication) {
        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        if (ta.getUserId().getUserId().equals(users.getUserId())) {
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
        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        if (ta.getUserId().getUserId().equals(users.getUserId())) {
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
        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        if (ta.getUserId().getUserId().equals(users.getUserId())) {
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
        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        if (ta.getUserId().getUserId().equals(users.getUserId())) {
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


