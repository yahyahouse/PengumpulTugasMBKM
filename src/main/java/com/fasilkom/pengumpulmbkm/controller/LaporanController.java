package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.config.AuthEntryPointJwt;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasilkom.pengumpulmbkm.model.Info.*;

@Tag(name = "4. Laporan MBKM",
        description = "API yang digunakan oleh role MAHASISWA untuk dapat melakukan CRUD pada entity Laporan")
@RestController
@RequestMapping("/mahasiswa/laporan")
public class LaporanController {
    private static final Logger LOG = LoggerFactory.getLogger(LaporanController.class);

    @Autowired
    private UsersService usersService;
    @Autowired
    private DosenService dosenService;
    @Autowired
    private LaporanService laporanService;
    @Autowired
    private ProgramService programService;

    @Operation(summary = "melakukan Upload Laporan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LaporanResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/upload-laporan")
    public ResponseEntity<LaporanResponse> uploadLaporan(
            @Parameter(description = "Masukan ID dosen sesuai dengan SK", example = "123")
            @NotBlank(message = "dosenId cannot be null")
            @RequestParam("dosenId") Integer dosenId,
            @Parameter(description = "Menambahkan Laporan", example = "hac verterem curae impetus aenean")
            @RequestParam("laporan") String laporanMBKM,
            @Parameter(description = "Masukan Program ID sesuai yang diikuti", example = "123")
            @RequestParam("program_id") Integer programId,
            Authentication authentication) {
        try {
            Users user = usersService.findByUsername(authentication.getName());
            Laporan laporan = new Laporan();
            Users users = usersService.findByUserId(user.getUserId());
            Dosen dosen = dosenService.getDosenByDosenId(dosenId);
            if (dosen == null) {
                return new ResponseEntity(new MessageResponse("Dosen Not Found"), HttpStatus.NOT_FOUND);
            }
            Program program = programService.findByProgramid(programId);
            if (program == null) {
                return new ResponseEntity(new MessageResponse("Program MBKM Not Found"), HttpStatus.NOT_FOUND);
            }
            LocalDateTime currentTime = LocalDateTime.now();
            laporan.setUserId(users);
            laporan.setDosenId(dosen);
            laporan.setProgramId(program);
            laporan.setLaporan(laporanMBKM);
            laporan.setVerifikasi(null);
            laporan.setWaktuPengumpulan(Timestamp.valueOf(currentTime));
            laporanService.saveLaporan(laporan);

            return new ResponseEntity(new LaporanResponse(laporan), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(String.valueOf(e));
            return new ResponseEntity(new MessageResponse("Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "melakukan Update Laporan ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LaporanResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Akses Ditolak",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/update-laporan/{laporanId}")
    public ResponseEntity<LaporanResponse> updatelaporan(
            @Parameter(description = "ID Laporan", example = "123")
            @PathVariable("laporanId") Integer laporanId,
            @Parameter(description = "Isi laporan", example = "consetetur elit sed ubique ferri")
            @RequestParam("laporan") String laporan,
            @Parameter(description = "ID Program MBKM", example = "123")
            @RequestParam("program_id") Integer programId,
            Authentication authentication
    ) {
        LocalDateTime currentTime = LocalDateTime.now();
        Laporan laporanSave = laporanService.findByLaporanId(laporanId);
        if (laporanSave == null) {
            return new ResponseEntity(new MessageResponse("Laporan Not Found"), HttpStatus.NOT_FOUND);
        }
        Program program = programService.findByProgramid(programId);
        if (program == null) {
            return new ResponseEntity(new MessageResponse("Program MBKM Not Found"), HttpStatus.NOT_FOUND);
        }
        Users users = usersService.findByUsername(authentication.getName());
        if (laporanSave.getUserId().getUserId().equals(users.getUserId())) {
            if (programId != null) {
                laporanSave.setProgramId(program);
            }
            laporanSave.setLaporan(laporan);
            laporanSave.setWaktuUpdate(Timestamp.valueOf(currentTime));
            laporanService.saveLaporan(laporanSave);
            return new ResponseEntity<>(new LaporanResponse(laporanSave), HttpStatus.OK);
        } else {
            return new ResponseEntity(new MessageResponse(AKSES_DITOLAK), HttpStatus.FORBIDDEN);
        }

    }

    @Operation(summary = "menampilkan daftar Laporan berdasarkan userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LaporanResponse.class)))
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LaporanResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Akses Ditolak",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @GetMapping("/detail-laporan/{laporanId}")
    public ResponseEntity<LaporanResponse> getLaporanByid(
            @Parameter(description = "ID laporan untuk mendapatkan laporan", example = "123")
            @PathVariable("laporanId") Integer laporanId,
            Authentication authentication) {
        try {
            Users users = usersService.findByUsername(authentication.getName());
            Laporan laporan = laporanService.findByLaporanId(laporanId);
            if (laporan == null) {
                return new ResponseEntity(new MessageResponse("Not Found"), HttpStatus.NOT_FOUND);
            }
            if (laporan.getUserId().getUserId().equals(users.getUserId())) {
                return new ResponseEntity<>(new LaporanResponse(laporan), HttpStatus.OK);
            } else
                return new ResponseEntity(new MessageResponse(AKSES_DITOLAK), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity(new MessageResponse("internal server error " + e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
