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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Operation(summary = "menampilkan file sertifikat")
    @GetMapping("/mahasiswa/sertifikat/{tugasAkhirId}")
    public ResponseEntity<Resource> displayFileSertifikat(@PathVariable Integer tugasAkhirId) {
        Optional<TugasAkhir> optionalDocument = Optional.ofNullable(tugasAkhirService.findByTugasAkhirId(tugasAkhirId));
        if (optionalDocument.isPresent()) {
            TugasAkhir tugasAkhir = optionalDocument.get();
            byte[] fileBytes = tugasAkhir.getSertifikat();

            ByteArrayResource resource = new ByteArrayResource(fileBytes);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.pdf");

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


