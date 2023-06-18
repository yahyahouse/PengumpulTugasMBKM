package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.response.TugasAkhirResponse;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
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

@Tag(name = "Tugas AKhir", description = "API for processing various operations with Tugas Akhir entity")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/tugas-akhir")
public class TugasAkhirController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private TugasAkhirService tugasAkhirService;

    @Operation(summary = "Add a new product by seller")
    @PostMapping("/mahasiswa/upload-tugas-akhir/{userId}")
    public ResponseEntity<TugasAkhirResponse> uploadTugasAkhir(
            @PathVariable("userId") Integer userId,
            @RequestParam("dosenId") Dosen dosenId,
            @RequestParam("sertifikat") String sertifikat,
            @RequestParam("lembarPengesahan") String lembarPengesahan,
            @RequestParam("nilai") String nilai,
            @RequestParam("laporanTugasAkhir") String laporanTugasAkhir,
            @RequestParam("verifikasi") Boolean verifikasi){
        TugasAkhir TA = new TugasAkhir();
        Users users = usersService.findByUserId(userId);
        TA.setUserId(users);
        TA.setDosenId(dosenId);
        TA.setSertifikat(sertifikat);
        TA.setLembarPengesahan(lembarPengesahan);
        TA.setNilai(nilai);
        TA.setLaporanTugasAkhir(laporanTugasAkhir);
        TA.setVerifikasi(verifikasi);
        tugasAkhirService.saveTugasAkhir(TA);

        return new ResponseEntity(new TugasAkhirResponse(TA), HttpStatus.OK);
    }

}
