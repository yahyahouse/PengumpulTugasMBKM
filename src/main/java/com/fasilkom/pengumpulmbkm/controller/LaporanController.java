package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.util.CommonConstant;
import com.fasilkom.pengumpulmbkm.model.response.BaseResponse;
import com.fasilkom.pengumpulmbkm.model.response.LaporanResponse;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.model.tugas.Laporan;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.LaporanService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import com.fasilkom.pengumpulmbkm.util.ResponseUtil;
import com.yahya.commonlogger.StructuredLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

import static com.fasilkom.pengumpulmbkm.util.CommonConstant.*;

@Tag(name = "4. Laporan MBKM",
        description = "API yang digunakan oleh role MAHASISWA for CRUD on entity Laporan")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LaporanController {


    private final StructuredLogger structuredLogger;
    private final UsersService usersService;
    private final LaporanService laporanService;

    @Operation(summary = "melakukan Upload Laporan")
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
    @PostMapping("/reports")
    public ResponseEntity<BaseResponse> uploadLaporan(
            @Parameter(description = "Masukan ID dosen sesuai dengan SK", example = "123")
            @NotBlank(message = "dosenId cannot be null")
            @RequestParam("dosenId") String dosenId,
            @Parameter(description = "Menambahkan Laporan", example = "hac verterem curae impetus aenean")
            @RequestParam("laporan") String laporanMBKM,
            @Parameter(description = "Masukan Program ID sesuai yang diikuti", example = "123")
            @RequestParam("program_id") Integer programId,
            Authentication authentication) {
        try {
            Users user = usersService.findByUsername(authentication.getName());
            Laporan laporan = laporanService.uploadLaporan(dosenId, laporanMBKM, programId, user);

            structuredLogger.newLog()
                    .withLogLevel(LogLevel.INFO)
                    .onSuccess(laporan, 0);

            return ResponseUtil.ok(new LaporanResponse(laporan));
        } catch (IllegalArgumentException e) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            structuredLogger.newLog()
                    .withLogLevel(LogLevel.ERROR)
                    .onFailure(e, 0);
            return ResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Operation(summary = "melakukan Update Laporan ")
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
    @PutMapping("/reports/{laporanId}")
    public ResponseEntity<BaseResponse> updatelaporan(
            @Parameter(description = "ID Laporan", example = "123")
            @PathVariable("laporanId") Integer laporanId,
            @Parameter(description = "Isi laporan", example = "consetetur elit sed ubique ferri")
            @RequestParam("laporan") String laporan,
            @Parameter(description = "ID Program MBKM", example = "123")
            @RequestParam("program_id") Integer programId,
            Authentication authentication
    ) {
        try {
            Users users = usersService.findByUsername(authentication.getName());
            Laporan laporanSave = laporanService.updateLaporanBusinessLogic(laporanId, laporan, programId, users);

            structuredLogger.newLog()
                    .withLogLevel(LogLevel.INFO)
                    .onSuccess(laporanSave, 0);

            return ResponseUtil.ok(new LaporanResponse(laporanSave));
        } catch (IllegalArgumentException e) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseUtil.error(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            structuredLogger.newLog()
                    .withLogLevel(LogLevel.ERROR)
                    .onFailure(e, 0);
            return ResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Operation(summary = "menampilkan daftar Laporan berdasarkan userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/reports/me")
    public ResponseEntity<BaseResponse> getLaporanByUserId(
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        List<Laporan> laporan = laporanService.findLaporanByUserId(users.getUserId());
        List<LaporanResponse> taGetResponse =
                laporan.stream().map(LaporanResponse::new).toList();

        return ResponseUtil.ok(taGetResponse);
    }

    @Operation(summary = "menampilkan detail Laporan ")
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
    public ResponseEntity<BaseResponse> getLaporanByid(
            @PathVariable @Parameter(description = "ID laporan for mendapatkan laporan", example = "123") Integer laporanId,
            Authentication authentication) {
        try {
            Users users = usersService.findByUsername(authentication.getName());
            Laporan laporan = laporanService.findByLaporanId(laporanId);
            if (laporan == null) {
                return ResponseUtil.error(HttpStatus.NOT_FOUND, CommonConstant.NOT_FOUND);
            }
            if (laporan.getUserId().getUserId().equals(users.getUserId())) {
                return ResponseUtil.ok(new LaporanResponse(laporan));
            } else
                return ResponseUtil.error(HttpStatus.FORBIDDEN, AKSES_DITOLAK);
        } catch (Exception e) {
            return ResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, "internal server error " + e);
        }
    }

}
