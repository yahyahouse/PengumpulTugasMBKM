package com.fasilkom.pengumpulmbkm.controller;


import com.fasilkom.pengumpulmbkm.model.response.BaseResponse;
import com.fasilkom.pengumpulmbkm.model.response.LaporanResponse;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.model.response.TugasAkhirResponse;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.tugas.TugasAkhir;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
import com.fasilkom.pengumpulmbkm.service.LaporanService;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.fasilkom.pengumpulmbkm.util.CommonConstant;

@Tag(name = "6. Dosen", description = "API yang digunakan untuk user yang memiliki role DOSEN")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lecturer")
public class DosenController {

    private final DosenService dosenService;

    private final UsersService usersService;

    private final LaporanService laporanService;

    private final TugasAkhirService tugasAkhirService;


    @Operation(summary = "Verifikasi Laporan MBKM menjadi diterima")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "403", description = CommonConstant.AKSES_DITOLAK,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = CommonConstant.NOT_FOUND,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
    })
    @PostMapping("/reports/{laporanId}/approve")
    public ResponseEntity<BaseResponse> verifikasiLaporanTrue(
            @PathVariable @Parameter(description = "ID Laporan yang ingin diverifikasi", example = "123") Integer laporanId,
            Authentication authentication) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (laporan == null) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, CommonConstant.NOT_FOUND);
        }
        if (laporan.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            LocalDateTime currentTime = LocalDateTime.now();
            laporan.setVerifikasi(true);
            laporan.setWaktuUpdate(Timestamp.valueOf(currentTime));
            laporanService.saveLaporan(laporan);
            return ResponseUtil.ok(new LaporanResponse(laporan));
        } else
            return ResponseUtil.error(HttpStatus.FORBIDDEN, CommonConstant.AKSES_DITOLAK);

    }

    @Operation(summary = "Verifikasi Laporan MBKM menjadi ditolak")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "403", description = CommonConstant.AKSES_DITOLAK,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = CommonConstant.NOT_FOUND,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/reports/{laporanId}/reject")
    public ResponseEntity<BaseResponse> verifikasiLaporanFalse(
            @PathVariable @Parameter(description = "ID Laporan yang ingin diverifikasi", example = "123") Integer laporanId,
            @Parameter(description = "menambahkan catatan", example = "laporan sama seperti sebelumnya")
            @RequestParam("catatan") String catatan,
            Authentication authentication) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (laporan == null) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, CommonConstant.NOT_FOUND);
        }
        if (laporan.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            LocalDateTime currentTime = LocalDateTime.now();
            laporan.setCatatan(catatan);
            laporan.setVerifikasi(false);
            laporan.setWaktuUpdate(Timestamp.valueOf(currentTime));
            laporanService.saveLaporan(laporan);

            return ResponseUtil.ok(new LaporanResponse(laporan));
        } else
            return ResponseUtil.error(HttpStatus.FORBIDDEN, CommonConstant.AKSES_DITOLAK);


    }

    @Operation(summary = "Verifikasi Tugas Akhir MBKM menjadi di terima")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "403", description = CommonConstant.AKSES_DITOLAK,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = CommonConstant.NOT_FOUND,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
    })
    @PostMapping("/final-projects/{tugasAkhirId}/approve")
    public ResponseEntity<BaseResponse> verifikasiTugasAkhirTrue(
            @PathVariable @Parameter(description = "ID Tugas Akhir yang ingin diverifikasi", example = "123") Integer tugasAkhirId,
            Authentication authentication) {
        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (ta == null) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, CommonConstant.NOT_FOUND);
        }
        if (ta.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            LocalDateTime currentTime = LocalDateTime.now();
            ta.setVerifikasi(true);
            ta.setWaktuUpdate(Timestamp.valueOf(currentTime));
            tugasAkhirService.saveTugasAkhir(ta);
            return ResponseUtil.ok(new TugasAkhirResponse(ta));
        } else
            return ResponseUtil.error(HttpStatus.FORBIDDEN, CommonConstant.AKSES_DITOLAK);


    }

    @Operation(summary = "Verifikasi Tugas Akhir MBKM menjadi ditolak")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "403", description = CommonConstant.AKSES_DITOLAK,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = CommonConstant.NOT_FOUND,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/final-projects/{tugasAkhirId}/reject")
    public ResponseEntity<BaseResponse> verifikasiTugasAkhirFalse(
            @PathVariable @Parameter(description = "ID Tugas Akhir yang ingin diverifikasi", example = "123") Integer tugasAkhirId,
            @Parameter(description = "menambahakan catatan", example = "gambar pada file laporan tidak jelas")
            @RequestParam("catatan") String catatan,
            Authentication authentication) {
        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (ta == null) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, CommonConstant.NOT_FOUND);
        }
        if (ta.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            LocalDateTime currentTime = LocalDateTime.now();
            ta.setCatatan(catatan);
            ta.setVerifikasi(false);
            ta.setWaktuUpdate(Timestamp.valueOf(currentTime));
            tugasAkhirService.saveTugasAkhir(ta);

            return ResponseUtil.ok(new TugasAkhirResponse(ta));
        } else
            return ResponseUtil.error(HttpStatus.FORBIDDEN, CommonConstant.AKSES_DITOLAK);


    }

    @Operation(summary = "menampilkan daftar Laporan berdasarkan userId dosen ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/reports")
    public ResponseEntity<BaseResponse> getLaporanByUserId(
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        List<Laporan> laporan = laporanService.findLaporanByDosenId(dosen.getDosenId());
        List<LaporanResponse> taGetResponse =
                laporan.stream().map(LaporanResponse::new).toList();

        return ResponseUtil.ok(taGetResponse);
    }

    @Operation(summary = "menampilkan daftar Tugas Akhir berdasarkan userId dosen ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/final-projects")
    public ResponseEntity<BaseResponse> getTugasAkhirByUserId(
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        List<TugasAkhir> ta = tugasAkhirService.getTugasAkhirByDosenId(dosen.getDosenId());
        List<TugasAkhirResponse> taGetResponse =
                ta.stream().map(TugasAkhirResponse::new).toList();

        return ResponseUtil.ok(taGetResponse);
    }

    @Operation(summary = "menampilkan detail Laporan Tugas Akhir berdasarkan tugasAkhirId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = CommonConstant.NOT_FOUND,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "403", description = CommonConstant.AKSES_DITOLAK,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/final-projects/{tugasAkhirId}")
    public ResponseEntity<BaseResponse> getDetailTugasAkhirById(
            @PathVariable @Parameter(description = "ID Tugas Akhir yang ingin ditampilkan", example = "123") Integer tugasAkhirId,
            Authentication authentication) {
        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (ta == null) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, CommonConstant.NOT_FOUND);
        }
        if (ta.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            return ResponseUtil.ok(new TugasAkhirResponse(ta));
        } else
            return ResponseUtil.error(HttpStatus.FORBIDDEN, CommonConstant.AKSES_DITOLAK);
    }

    @Operation(summary = "menampilkan detail Laporan berdasarkan laporanId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = CommonConstant.NOT_FOUND,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "403", description = CommonConstant.AKSES_DITOLAK,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/reports/{laporanId}")
    public ResponseEntity<BaseResponse> getDetailLaporanById(
            @PathVariable @Parameter(description = "ID Tugas Akhir yang ingin ditampilkan", example = "123") Integer laporanId,
            Authentication authentication) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (laporan == null) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, CommonConstant.NOT_FOUND);
        }
        if (laporan.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            return ResponseUtil.ok(new LaporanResponse(laporan));
        }
        return ResponseUtil.error(HttpStatus.FORBIDDEN, CommonConstant.AKSES_DITOLAK);
    }

}
