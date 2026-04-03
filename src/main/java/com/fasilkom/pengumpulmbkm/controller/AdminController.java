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
import com.fasilkom.pengumpulmbkm.util.CommonConstant;
import com.fasilkom.pengumpulmbkm.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "4. Admin", description = "API yang digunakan oleh role ADMIN untuk dapat melakukan CRUD data Master ")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final DosenService dosenService;

    private final UsersService usersService;

    private final LaporanService laporanService;

    private final TugasAkhirService tugasAkhirService;

    @Operation(summary = "Melakukan add Dosen ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/lecturers/{userId}")
    public ResponseEntity<BaseResponse> addDosen(@PathVariable @Parameter(description = "User Id yang akan dijadikan dosen", example = "123") String userId) {
        Users users = usersService.findByUserId(userId);
        if (users == null) {
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, "cannot find user!!");
        }
        Dosen isDosenExist = dosenService.getDosenByUserId(userId);
        if (isDosenExist != null) {
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, "the lecturer already exists in the database!!");
        }
        Dosen dosen = new Dosen();
        dosen.setUserId(users);
        dosenService.saveDosen(dosen);
        return ResponseUtil.ok("Successfully Added Lecturer", null);
    }

    @Operation(summary = "Melakukan Delete Dosen ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponse.class)))
    })
    @DeleteMapping("/lecturers/{dosenId}")
    public ResponseEntity<BaseResponse> deleteDosen(@PathVariable @Parameter(description = "Dosen Id yang akan dihapus", example = "123") String dosenId) {
        try {
            boolean existDosen = dosenService.existsDosenByDosenId(dosenId);
            if (existDosen) {
                List<TugasAkhir> tugasAkhir = tugasAkhirService.getTugasAkhirByDosenId(dosenId);
                List<Laporan> laporan = laporanService.findLaporanByDosenId(dosenId);
                if (tugasAkhir.isEmpty() && laporan.isEmpty()) {
                    dosenService.deletDosenByDosenId(dosenId);
                    return ResponseUtil.ok("Successfully delete Lecturer", null);
                }
                return ResponseUtil.error(HttpStatus.BAD_REQUEST, "Lecturer cannot be deleted because they are assigned to a Final Project or Report");
            }
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, CommonConstant.NOT_FOUND);
        } catch (Exception e) {
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, CommonConstant.NOT_FOUND);
        }
    }

    @Operation(summary = "Menampilkan Seluruh User yang Terdaftar ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponse.class))),
    })
    @GetMapping("/users")
    public ResponseEntity<BaseResponse> getAllUsers() {
        List<Users> users = usersService.getAllUsers();
        List<UsersResponse> allUsers = users.stream().map(UsersResponse::new).toList();
        return ResponseUtil.ok(allUsers);
    }

    @Operation(summary = "Menampilkan Seluruh Laporan Mahasiswa ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponse.class))),
    })
    @GetMapping("/reports")
    public ResponseEntity<BaseResponse> getAllLaporan() {
        List<LaporanResponse> laporan = laporanService.getAllLaporan().stream().map(LaporanResponse::new).toList();
        return ResponseUtil.ok(laporan);
    }

    @Operation(summary = "Menampilkan Seluruh Tugas Akhir Mahasiswa ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponse.class))),
    })
    @GetMapping("/final-projects")
    public ResponseEntity<BaseResponse> getAllTugasAkhir() {
        List<TugasAkhirResponse> ta = tugasAkhirService.getAllTugasAkhir().stream().map(TugasAkhirResponse::new).toList();
        return ResponseUtil.ok(ta);
    }

    @Operation(summary = "menampilkan detail Laporan Tugas Akhir berdasarkan tugasAkhirId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = CommonConstant.NOT_FOUND,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
    })
    @GetMapping("/final-projects/{tugasAkhirId}")
    public ResponseEntity<BaseResponse> getDetailTugasAkhirById(
            @PathVariable @Parameter(description = "ID tugas akhir ") Integer tugasAkhirId) {
        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        if (ta == null) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, CommonConstant.NOT_FOUND);
        } else {
            return ResponseUtil.ok(new TugasAkhirResponse(ta));
        }
    }

    @Operation(summary = "menampilkan detail Laporan berdasarkan laporanId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = CommonConstant.NOT_FOUND,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
    })
    @GetMapping("/reports/{laporanId}")
    public ResponseEntity<BaseResponse> getDetailLaporanById(
            @PathVariable @Parameter(description = "ID Laporan ") Integer laporanId) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        if (laporan == null) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, CommonConstant.NOT_FOUND);
        } else {
            return ResponseUtil.ok(new LaporanResponse(laporan));
        }
    }

}
