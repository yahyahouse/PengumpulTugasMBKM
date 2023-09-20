package com.fasilkom.pengumpulmbkm.controller;


import com.fasilkom.pengumpulmbkm.model.response.*;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.LaporanService;
import com.fasilkom.pengumpulmbkm.service.TugasAkhirService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "7. Admin", description = "API yang dapat digunakan oleh role ADMIN")
@RestController
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


    @Operation(summary = "menambahkan dosen dengan cara memasukan user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Added Lecturer",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping(value = "/add-dosen")
    public ResponseEntity<MessageResponse> addDosen(
            @Parameter(description = "ID user yang akan di tambah menjadi dosen", example = "123")
            @RequestParam("userId") Integer userId) {
        if (usersService.findByUserId(userId) == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("cannot find user!!"));
        }
        if (dosenService.getDosenByUserId(userId) != null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("the lecturer already exists in the database!!"));
        }
        Dosen dosenAdd = new Dosen();
        Users users = usersService.findByUserId(userId);
        dosenAdd.setUserId(users);
        dosenService.saveDosen(dosenAdd);
        return ResponseEntity.ok(new MessageResponse("Successfully Added Lecturer"));
    }

    @Operation(summary = "menghapus dosen sesuai id dosen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully delete Lecturer",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class)))
    })
    @DeleteMapping(value = "/delete-dosen")
    public ResponseEntity<MessageResponse> deleteDosen(
            @Parameter(description = "ID dosen yang akan di hapus", example = "123")
            @RequestParam("dosenId") Integer dosenId) {
        try {
            if (!dosenService.existsDosenByDosenId(dosenId)) {
                return new ResponseEntity(new MessageResponse("Not Found"), HttpStatus.BAD_REQUEST);
            }
            if (tugasAkhirService.getTugasAkhirByDosenId(dosenId).isEmpty() || laporanService.findLaporanByDosenId(dosenId).isEmpty()) {
                dosenService.deletDosenByDosenId(dosenId);
                return ResponseEntity.ok(new MessageResponse("Successfully delete Lecturer"));
            }
            return new ResponseEntity(new MessageResponse("Cannot delete lecturer with id " + dosenId), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Internal Server Error"));
        }

    }

    @Operation(summary = "Menampilkan semua users yang terdaftar pasa sistem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User List",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UsersResponse.class)))
    })
    @GetMapping(value = "/all-users")
    public ResponseEntity<List<UsersResponse>> getAllUsers() {
        List<Users> users = usersService.getAllUsers();
        List<UsersResponse> allUsers =
                users.stream().map(UsersResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @Operation(summary = "Menampilkan semua laporan yang di unggal oleh mahasiswa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List Laporan",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LaporanResponse.class)))
    })
    @GetMapping(value = "/all-laporan")
    public ResponseEntity<List<LaporanResponse>> getAllLaporan() {
        List<Laporan> laporans = laporanService.getAllLaporan();
        List<LaporanResponse> laporan =
                laporans.stream().map(LaporanResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(laporan, HttpStatus.OK);
    }

    @Operation(summary = "Menampilkan semua Tugas Akhir yang di unggal oleh mahasiswa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List Tugas Akhir",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TugasAkhirResponse.class)))
    })
    @GetMapping(value = "/all-tugas-akhir")
    public ResponseEntity<List<TugasAkhirResponse>> getAllTugasAkhir() {
        List<TugasAkhir> tas = tugasAkhirService.getAllTugasAkhir();
        List<TugasAkhirResponse> ta =
                tas.stream().map(TugasAkhirResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(ta, HttpStatus.OK);
    }

    @Operation(summary = "Menampilkan detail Tugas Akhir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully delete Lecturer",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TugasAkhirResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class)))
    })
    @GetMapping(value = "/detail-tugas-akhir/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirResponse> getDetailTugasAkhirById(
            @Parameter(description = "ID Tugas Akhir yang ingin ditampilkan", example = "123")
            @PathVariable("tugasAkhirId") Integer tugasAkhirId) {
        TugasAkhir tugasAkhir = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        if (tugasAkhir == null) {
            return new ResponseEntity(new MessageResponse("Not Found"), HttpStatus.NOT_FOUND);
        } else {
            TugasAkhirResponse response = new TugasAkhirResponse(tugasAkhir);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @Operation(summary = "Menampilkan detail Laporan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully delete Lecturer",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LaporanResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MessageResponse.class)))
    })
    @GetMapping(value = "/detail-laporan/{laporanId}")
    public ResponseEntity<LaporanResponse> getDetailLaporanById(
            @Parameter(description = "ID Laporan yang ingin ditampilkan", example = "123")
            @PathVariable("laporanId") Integer laporanId) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        if (laporan == null) {
            return new ResponseEntity(new MessageResponse("Not Found"), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(new LaporanResponse(laporan), HttpStatus.OK);
        }
    }

}
