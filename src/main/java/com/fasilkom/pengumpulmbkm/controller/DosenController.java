package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.MessageResponse;
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
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

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

    @Operation(summary = "Verifikasi Laporan MBKM")
    @PostMapping("/verifikasi-laporan/{laporanId}")
    public ResponseEntity<LaporanResponse> verifikasiLaporan(
            @PathVariable("laporanId") Integer laporanId) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        LocalDateTime currentTime = LocalDateTime.now();
        laporan.setVerifikasi(true);
        laporan.setWaktuUpdate(Timestamp.valueOf(currentTime));
        laporanService.saveLaporan(laporan);

        return new ResponseEntity(new LaporanResponse(laporan), HttpStatus.OK);
    }

    @Operation(summary = "Verifikasi Tugas Akhir MBKM")
    @PostMapping("/verifikasi-tugas-akhir/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirResponse> verifikasiTugasAkhir(
            @PathVariable("tugasAkhirId") Integer tugasAkhirId) {
        TugasAkhir TA = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        LocalDateTime currentTime = LocalDateTime.now();
        TA.setVerifikasi(true);
        TA.setWaktuUpdate(Timestamp.valueOf(currentTime));
        tugasAkhirService.saveTugasAkhir(TA);

        return new ResponseEntity(new TugasAkhirResponse(TA), HttpStatus.OK);
    }

}
