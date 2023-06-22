package com.fasilkom.pengumpulmbkm.controller;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Tugas AKhir MBKM", description = "API for processing various operations with Tugas Akhir entity")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/tugas-akhir")
public class TugasAkhirController {

    @Autowired
    private UsersService usersService;
    @Autowired
    private DosenService dosenService;

    @Autowired
    private TugasAkhirService tugasAkhirService;

    @Operation(summary = "Upload Tugas Akhir")
    @PostMapping("/mahasiswa/upload-tugas-akhir/{userId}")
    public ResponseEntity<TugasAkhirResponse> uploadTugasAkhir(
            @PathVariable("userId") Integer userId,
            @RequestParam("dosenId") Integer dosenId,
            @RequestParam("sertifikat") MultipartFile sertifikat,
            @RequestParam("lembarPengesahan") MultipartFile lembarPengesahan,
            @RequestParam("nilai") MultipartFile nilai,
            @RequestParam("laporanTugasAkhir") MultipartFile laporanTugasAkhir) throws IOException {
        try {
            TugasAkhir TA = new TugasAkhir();
            Users users = usersService.findByUserId(userId);
            Dosen dosen = dosenService.getDosenByUserId(dosenId);
            TA.setUserId(users);
            TA.setDosenId(dosen);
            TA.setSertifikat(sertifikat.getBytes());
            TA.setLembarPengesahan(lembarPengesahan.getBytes());
            TA.setNilai(nilai.getBytes());
            TA.setLaporanTugasAkhir(laporanTugasAkhir.getBytes());
            TA.setVerifikasi(false);
            tugasAkhirService.saveTugasAkhir(TA);


            return new ResponseEntity(new TugasAkhirResponse(TA), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(summary = "Update Laporan Tugas Akhir")
    @PostMapping("/mahasiswa/update-tugas-akhri/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirResponse> updateStatusTiket(
            @PathVariable("tugasAkhirId") Integer tugasAkhirtId,
            @RequestParam("sertifikat") MultipartFile sertifikat,
            @RequestParam("lembarPengesahan") MultipartFile lembarPengesahan,
            @RequestParam("nilai") MultipartFile nilai,
            @RequestParam("laporanTugasAkhir") MultipartFile laporanTugasAkhir
    ){
        try {
            TugasAkhir TA = tugasAkhirService.findByTugasAkhirId(tugasAkhirtId);
            TA.setSertifikat(sertifikat.getBytes());
            TA.setLembarPengesahan(lembarPengesahan.getBytes());
            TA.setNilai(nilai.getBytes());
            TA.setLaporanTugasAkhir(laporanTugasAkhir.getBytes());
            tugasAkhirService.updateTugasAkhir(TA);

            return new ResponseEntity(new TugasAkhirResponse(TA),HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @Operation(summary = "menampilkan detail Laporan Tugas Akhir")
    @GetMapping("/mahasiswa/detail-tugas-akhir/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirResponse> getTugasAkhirByid(
            @PathVariable("tugasAkhirId") Integer tugasAkhirId) {
        TugasAkhir TA = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);

        return new ResponseEntity(new TugasAkhirResponse(TA), HttpStatus.OK);
    }

    @GetMapping("/mahasiswa/list-tugas-akhir/{userId}")
    public ResponseEntity<TugasAkhirResponse> getTiketByUserId(
            @PathVariable("userId") Integer userId) {
        List<TugasAkhir> tiket = tugasAkhirService.getTugasAkhirByUserId(userId);
        List<TugasAkhirResponse> TAGetResponse =
                tiket.stream().map(TugasAkhirResponse::new).collect(Collectors.toList());

        return new ResponseEntity(TAGetResponse, HttpStatus.OK);
    }

    @Operation(summary = "menampilkan file sertifikat")
    @GetMapping("/mahasiswa/sertifikat/{tugasAkhirId}")
    public ResponseEntity<Resource> displayFileSertifikat(@PathVariable Integer tugasAkhirId) {
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
    }

    @Operation(summary = "menampilkan file nilai")
    @GetMapping("/mahasiswa/nilai/{tugasAkhirId}")
    public ResponseEntity<Resource> displayFileNilai(@PathVariable Integer tugasAkhirId) {
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
    }

    @Operation(summary = "menampilkan file Lembar Pengesahan")
    @GetMapping("/mahasiswa/lembar-pengesahan/{tugasAkhirId}")
    public ResponseEntity<Resource> displayFileLembarPengesahan(@PathVariable Integer tugasAkhirId) {
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
    }

    @Operation(summary = "menampilkan file Laporan Tugas Akhir")
    @GetMapping("/mahasiswa/laporan-tugas-akhir/{tugasAkhirId}")
    public ResponseEntity<Resource> displayFileTugasAkhir(@PathVariable Integer tugasAkhirId) {
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
    }
}


