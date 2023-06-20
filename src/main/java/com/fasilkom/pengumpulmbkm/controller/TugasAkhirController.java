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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

}
