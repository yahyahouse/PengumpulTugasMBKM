package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.response.LaporanResponse;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.LaporanService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Laporan MBKM", description = "API for processing various operations with Laporan entity")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/laporan")
public class LaporanController {

    @Autowired
    private UsersService usersService;
    @Autowired
    private DosenService dosenService;

    @Autowired
    private LaporanService laporanService;

    @Operation(summary = "Upload Laporan")
    @PostMapping("/mahasiswa/upload-laporan/{userId}")
    public ResponseEntity<LaporanResponse> uploadLaporan(
            @PathVariable("userId") Integer userId,
            @RequestParam("dosenId") Integer dosenId,
            @RequestParam("laporan") String laporanMBKM) {
        Laporan laporan = new Laporan();
        Users users = usersService.findByUserId(userId);
        Dosen dosen = dosenService.getDosenByUserId(dosenId);
        LocalDateTime currentTime = LocalDateTime.now();
        laporan.setUserId(users);
        laporan.setDosenId(dosen);
        laporan.setLaporan(laporanMBKM);
        laporan.setVerifikasi(false);
        laporan.setWaktuPengumpulan(Timestamp.valueOf(currentTime));
        laporanService.saveLaporan(laporan);

        return new ResponseEntity(new LaporanResponse(laporan), HttpStatus.OK);
    }
    @Operation(summary = "menampilkan daftar Laporan berdasarkan userId")
    @GetMapping("/mahasiswa/list-laporan/{userId}")
    public ResponseEntity<LaporanResponse> getLaporanByUserId(
            @PathVariable("userId") Integer userId) {
        List<Laporan> laporan = laporanService.findLaporanByUserId(userId);
        List<LaporanResponse> TAGetResponse =
                laporan.stream().map(LaporanResponse::new).collect(Collectors.toList());

        return new ResponseEntity(TAGetResponse, HttpStatus.OK);
    }

    @Operation(summary = "menampilkan detail Laporan ")
    @GetMapping("/mahasiswa/detail-laporan/{laporanId}")
    public ResponseEntity<LaporanResponse> getLaporanByid(
            @PathVariable("laporanId") Integer laporanId) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);

        return new ResponseEntity(new LaporanResponse(laporan), HttpStatus.OK);
    }

}