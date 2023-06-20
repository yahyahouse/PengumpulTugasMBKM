package com.fasilkom.pengumpulmbkm.controller;

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

@Tag(name = "Laporan MBKM", description = "API for processing various operations with Tugas Akhir entity")
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
    public ResponseEntity<LaporanResponse> uploadTugasAkhir(
            @PathVariable("userId") Integer userId,
            @RequestParam("dosenId") Integer dosenId,
            @RequestParam("laporanTugasAkhir") String laporanMBKM) {
        Laporan laporan = new Laporan();
        Users users = usersService.findByUserId(userId);
        Dosen dosen = dosenService.getDosenByUserId(dosenId);
        laporan.setUserId(users);
        laporan.setDosenId(dosen);
        laporan.setLaporan(laporanMBKM);
        laporan.setVerifikasi(false);
        laporanService.saveLaporan(laporan);

        return new ResponseEntity(new LaporanResponse(laporan), HttpStatus.OK);
    }

}
