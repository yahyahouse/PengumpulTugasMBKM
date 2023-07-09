package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.UserDetailsImpl;
import com.fasilkom.pengumpulmbkm.model.response.*;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.repository.TugasAkhirRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Admin", description = "API for processing various operations with Dosen entity")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private DosenService dosenService;
    @Autowired
    private UsersService usersService;
    @Autowired
    private LaporanService laporanService;
    @Autowired
    private TugasAkhirService tugasAkhirService;

    @Operation(summary = "Get detail dosen")
    @GetMapping(value = "/detail-profil-dosen/{dosenId}")
    public ResponseEntity<DosenResponse> getDetailDosenByDosenId(@PathVariable("dosenId") Integer dosenId) {
        Dosen dosen = dosenService.getDosenByDosenId(dosenId);
        return new ResponseEntity<>(new DosenResponse(dosen), HttpStatus.OK);
    }

    @Operation(summary = "add dosen")
    @PostMapping(value = "/add-dosen")
    public ResponseEntity<MessageResponse> addDosen(
            @RequestParam("userId") Integer userId) {
        Dosen dosen = dosenService.getDosenByUserId(userId);
        if (usersService.findByUserId(userId)==null){
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("cannot find user!!"));
        }
        if (dosen != null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("the lecturer already exists in the database!!"));
        }
        Dosen dosenAdd = new Dosen();
        Users users = usersService.findByUserId(userId);
        dosenAdd.setUserId(users);
        dosenService.saveDosen(dosenAdd);
        return ResponseEntity.ok(new MessageResponse("Successfully Added Lecturer"));


    }

    @Operation(summary = "delete dosen")
    @DeleteMapping(value = "/delete-dosen")
    public ResponseEntity<MessageResponse> deleteDosen(
            @RequestParam("dosenId") Integer dosenId) {
        try {
            dosenService.deletDosenByDosenId(dosenId);
            return ResponseEntity.ok(new MessageResponse("Successfully delete Lecturer"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse("Cannot delete lecturer with id" + dosenId));
        }

    }

    @Operation(summary = "Get all dosen")
    @GetMapping(value = "/all-dosen")
    public ResponseEntity<List<DosenResponse>> getAllDosen() {
        List<Dosen> dosen = dosenService.getAllDosen();
        List<DosenResponse> allDosen =
                dosen.stream().map(DosenResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(allDosen, HttpStatus.OK);
    }

    @Operation(summary = "Get all users")
    @GetMapping(value ="/all-users")
    public ResponseEntity<List<UsersResponse>> getAllUsers() {
        List<Users> users = usersService.getAllUsers();
        List<UsersResponse> allUsers =
                users.stream().map(UsersResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @Operation(summary = "Get all laporan")
    @GetMapping(value = "/all-laporan")
    public ResponseEntity<List<LaporanResponse>> getAllLaporan() {
        List<Laporan> laporans = laporanService.getAllLaporan();
        List<LaporanResponse> laporan =
                laporans.stream().map(LaporanResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(laporan, HttpStatus.OK);
    }

    @Operation(summary = "Get all Tugas Akhir")
    @GetMapping(value = "/all-tugas-akhir")
    public ResponseEntity<List<TugasAkhirResponse>> getAllTugasAkhir() {
        List<TugasAkhir> TAs = tugasAkhirService.getAllTugasAkhir();
        List<TugasAkhirResponse> TA =
                TAs.stream().map(TugasAkhirResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(TA, HttpStatus.OK);
    }

    @Operation(summary = "Get detail Tugas Akhir")
    @GetMapping(value = "/detail-tugas-akhir/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirResponse> getDetailTugasAkhirById(@PathVariable("tugasAkhirId") Integer tugasAkhirId) {
        TugasAkhir tugasAkhir = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        return new ResponseEntity<>(new TugasAkhirResponse(tugasAkhir), HttpStatus.OK);
    }

    @Operation(summary = "Get detail Laporan")
    @GetMapping(value = "/detail-laporan/{laporanId}")
    public ResponseEntity<LaporanResponse> getDetailLaporanById(@PathVariable("laporanId") Integer laporanId) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        return new ResponseEntity<>(new LaporanResponse(laporan), HttpStatus.OK);
    }

}
