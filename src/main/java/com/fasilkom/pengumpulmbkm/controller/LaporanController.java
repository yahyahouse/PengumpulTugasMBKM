package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.response.LaporanResponse;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.LaporanService;
import com.fasilkom.pengumpulmbkm.service.ProgramService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasilkom.pengumpulmbkm.model.Info.*;

@Tag(name = "Laporan MBKM", description = "API for processing various operations with Laporan entity")
@Order(4)
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/mahasiswa/laporan")
public class LaporanController {

    @Autowired
    private UsersService usersService;
    @Autowired
    private DosenService dosenService;
    @Autowired
    private LaporanService laporanService;
    @Autowired
    private ProgramService programService;

    @Operation(summary = "Upload Laporan")
    @PostMapping("/upload-laporan")
    public ResponseEntity<LaporanResponse> uploadLaporan(
            @Parameter(description = "Masukan ID dosen sesuai dengan SK", example = "123")
            @RequestParam("dosenId") Integer dosenId,
            @Parameter(description = "Menambahkan Laporan", example = "hac verterem curae impetus aenean")
            @RequestParam("laporan") String laporanMBKM,
            @Parameter(description = "Masukan Program ID sesuai yang diikuti", example = "123")
            @RequestParam("program_id") Integer programId,
            Authentication authentication) {
        Users user = usersService.findByUsername(authentication.getName());
        Laporan laporan = new Laporan();
        Users users = usersService.findByUserId(user.getUserId());
        Dosen dosen = dosenService.getDosenByDosenId(dosenId);
        Program program = programService.findByProgramid(programId);
        LocalDateTime currentTime = LocalDateTime.now();
        laporan.setUserId(users);
        laporan.setDosenId(dosen);
        laporan.setProgramId(program);
        laporan.setLaporan(laporanMBKM);
        laporan.setVerifikasi(null);
        laporan.setWaktuPengumpulan(Timestamp.valueOf(currentTime));
        laporanService.saveLaporan(laporan);

        return new ResponseEntity(new LaporanResponse(laporan), HttpStatus.OK);
    }

    @Operation(summary = "Update Laporan ")
    @PostMapping("/update-laporan/{laporanId}")
    public ResponseEntity<LaporanResponse> updatelaporan(
            @Parameter(description = "ID Tugas Akhir yang ingin ditampilkan", example = "123")
            @PathVariable("laporanId") Integer laporanId,
            @Parameter(description = "Update Laporan", example = "consetetur elit sed ubique ferri")
            @RequestParam("laporan") String laporan,
            @Parameter(description = "ID Tugas Akhir yang ingin ditampilkan", example = "123")
            @RequestParam("program_id")Integer programId,
            Authentication authentication
    ) {
        LocalDateTime currentTime = LocalDateTime.now();
        Laporan laporanSave = laporanService.findByLaporanId(laporanId);
        Users users = usersService.findByUsername(authentication.getName());
        if (laporanSave.getUserId().getUserId().equals(users.getUserId())) {
            if (programId !=null){
                Program program =programService.findByProgramid(programId);
                laporanSave.setProgramId(program);
            }
            laporanSave.setLaporan(laporan);
            laporanSave.setWaktuUpdate(Timestamp.valueOf(currentTime));
            laporanService.saveLaporan(laporanSave);
            return new ResponseEntity<>(new LaporanResponse(laporanSave), HttpStatus.OK);
        } else {
            return new ResponseEntity(new MessageResponse(AKSES_DITOLAK), HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
        }

    }

    @Operation(summary = "menampilkan daftar Laporan berdasarkan userId")
    @GetMapping("/list-laporan")
    public ResponseEntity<LaporanResponse> getLaporanByUserId(
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        List<Laporan> laporan = laporanService.findLaporanByUserId(users.getUserId());
        List<LaporanResponse> taGetResponse =
                laporan.stream().map(LaporanResponse::new).collect(Collectors.toList());

        return new ResponseEntity(taGetResponse, HttpStatus.OK);
    }

    @Operation(summary = "menampilkan detail Laporan ")
    @GetMapping("/detail-laporan/{laporanId}")
    public ResponseEntity<LaporanResponse> getLaporanByid(
            @Parameter(description = "ID laporan untuk mendapatkan laporan", example = "123")
            @PathVariable("laporanId") Integer laporanId,
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        if (laporan.getUserId().getUserId().equals(users.getUserId())) {
            return new ResponseEntity<>(new LaporanResponse(laporan), HttpStatus.OK);
        } else
            return new ResponseEntity(new MessageResponse(AKSES_DITOLAK), HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
    }

}
