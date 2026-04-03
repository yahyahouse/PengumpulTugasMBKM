package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.util.CommonConstant;
import com.fasilkom.pengumpulmbkm.model.response.BaseResponse;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.model.response.TugasAkhirGetDetailResponse;
import com.fasilkom.pengumpulmbkm.model.response.TugasAkhirResponse;
import com.fasilkom.pengumpulmbkm.model.roles.Program;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.ProgramService;
import com.fasilkom.pengumpulmbkm.service.TugasAkhirService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasilkom.pengumpulmbkm.util.CommonConstant.*;

@Tag(name = "5. Tugas Akhir MBKM",
        description = "API yang digunakan oleh role MAHASISWA untuk dapat melakukan CRUD pada entity Tugas Akhir")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TugasAkhirController {

    private final UsersService usersService;
    private final DosenService dosenService;
    private final ProgramService programService;
    private final TugasAkhirService tugasAkhirService;

    @Operation(summary = "Melakukan Upload Tugas Akhir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = CommonConstant.NOT_FOUND,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping(value = "/final-projects", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> uploadTugasAkhir(
            @Parameter(description = "ID Dosen sesuai dengan SK", example = "123")
            @NotNull(message = "dosenId cannot be null")
            @RequestParam("dosenId") String dosenId,
            @Parameter(description = "ID program sesuai dengan yang diikuti", example = "123")
            @RequestParam("programId") Integer programId,
            @Parameter(description = "File sertifikat")
            @NotNull
            @RequestParam("sertifikat") MultipartFile sertifikat,
            @Parameter(description = "File lembar pengesahan")
            @NotNull
            @RequestParam("lembarPengesahan") MultipartFile lembarPengesahan,
            @Parameter(description = "File nilai")
            @NotNull
            @RequestParam("nilai") MultipartFile nilai,
            @Parameter(description = "File Laporan Tugas Akhir")
            @NotNull
            @RequestParam("laporanTugasAkhir") MultipartFile laporanTugasAkhir,
            Authentication authentication) {
        try {
            Users user = usersService.findByUsername(authentication.getName());
            TugasAkhir ta = new TugasAkhir();
            Users users = usersService.findByUserId(user.getUserId());
            Dosen dosen = dosenService.getDosenByDosenId(dosenId);
            Program program = programService.findByProgramid(programId);
            if (dosen == null) {
                return ResponseUtil.error(HttpStatus.NOT_FOUND, DOSEN_NOT_FOUND);
            }
            if (program == null) {
                return ResponseUtil.error(HttpStatus.NOT_FOUND, PROGRAM_MBKM_NOT_FOUND);
            }
            LocalDateTime currentTime = LocalDateTime.now();
            ta.setUserId(users);
            ta.setDosenId(dosen);
            ta.setProgramId(program);
            ta.setSertifikat(sertifikat.getBytes());
            ta.setLembarPengesahan(lembarPengesahan.getBytes());
            ta.setNilai(nilai.getBytes());
            ta.setLaporanTugasAkhir(laporanTugasAkhir.getBytes());
            ta.setVerifikasi(null);
            ta.setWaktuPengumpulan(Timestamp.valueOf(currentTime));
            tugasAkhirService.saveTugasAkhir(ta);
            return ResponseUtil.ok(new TugasAkhirResponse(ta));
        } catch (IOException e) {
            return ResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Operation(summary = "Melakukan Update Laporan Tugas Akhir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = CommonConstant.NOT_FOUND,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "403", description = AKSES_DITOLAK,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class)))
    })
    @PutMapping(value = "/final-projects/{tugasAkhirId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> updateTugasAkhir(
            @PathVariable @Parameter(description = "ID tugas akhir yang diupdate", example = "123") Integer tugasAkhirId,
            @Parameter(description = "ID program yang ingin diupdate", example = "123")
            @RequestParam("programId") Integer programId,
            @Parameter(description = "File sertifikat")
            @RequestParam(value = "sertifikat", required = false) MultipartFile sertifikat,
            @Parameter(description = "File lembar pengesahan")
            @RequestParam(value = "lembarPengesahan", required = false) MultipartFile lembarPengesahan,
            @Parameter(description = "File nilai")
            @RequestParam(value = "nilai", required = false) MultipartFile nilai,
            @Parameter(description = "File laporan tugas akhir")
            @RequestParam(value = "laporanTugasAkhir", required = false) MultipartFile laporanTugasAkhir,
            Authentication authentication
    ) {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
            Program program = programService.findByProgramid(programId);
            if (ta == null) {
                return ResponseUtil.error(HttpStatus.NOT_FOUND, TUGAS_AKHIR_NOT_FOUND);
            }
            if (program == null) {
                return ResponseUtil.error(HttpStatus.NOT_FOUND, PROGRAM_MBKM_NOT_FOUND);
            }
            Users users = usersService.findByUsername(authentication.getName());
            if (!ta.getUserId().getUserId().equals(users.getUserId())) {
                return ResponseUtil.error(HttpStatus.FORBIDDEN, AKSES_DITOLAK);
            }
            if (sertifikat != null) {
                ta.setSertifikat(sertifikat.getBytes());
            }
            if (lembarPengesahan != null) {
                ta.setLembarPengesahan(lembarPengesahan.getBytes());
            }
            if (nilai != null) {
                ta.setNilai(nilai.getBytes());
            }
            if (laporanTugasAkhir != null) {
                ta.setLaporanTugasAkhir(laporanTugasAkhir.getBytes());
            }
            ta.setProgramId(program);
            ta.setWaktuUpdate(Timestamp.valueOf(currentTime));
            tugasAkhirService.saveTugasAkhir(ta);
            return ResponseUtil.ok(new TugasAkhirResponse(ta));
        } catch (IOException e) {
            return ResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Operation(summary = "menampilkan detail Laporan Tugas Akhir berdasarkan tugasAkhirId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = CommonConstant.NOT_FOUND,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "403", description = AKSES_DITOLAK,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/final-projects/{tugasAkhirId}")
    public ResponseEntity<BaseResponse> getTugasAkhirByid(
            @PathVariable @Parameter(description = "ID tugas akhir ") Integer tugasAkhirId,
            Authentication authentication) {
        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        if (ta == null) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, TUGAS_AKHIR_NOT_FOUND);
        }
        Users users = usersService.findByUsername(authentication.getName());
        if (ta.getUserId().getUserId().equals(users.getUserId())) {
            return ResponseUtil.ok(new TugasAkhirGetDetailResponse(ta));
        } else
            return ResponseUtil.error(HttpStatus.FORBIDDEN, AKSES_DITOLAK);
    }

    @Operation(summary = "menampilkan daftar laporan sesuai season login ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = CommonConstant.NOT_FOUND,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
    })
    @GetMapping("/final-projects/me")
    public ResponseEntity<BaseResponse> getTugasAkhirByUserId(
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        List<TugasAkhir> ta = tugasAkhirService.getTugasAkhirByUserId(users.getUserId());
        if (ta == null) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, TUGAS_AKHIR_NOT_FOUND);
        }
        List<TugasAkhirResponse> taGetResponse =
                ta.stream().map(TugasAkhirResponse::new).toList();

        return ResponseUtil.ok(taGetResponse);
    }
}
