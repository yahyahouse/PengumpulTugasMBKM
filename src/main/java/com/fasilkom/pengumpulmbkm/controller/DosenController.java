package com.fasilkom.pengumpulmbkm.controller;


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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasilkom.pengumpulmbkm.model.Info.AKSES_DITOLAK;

@Tag(name = "6. Dosen", description = "API yang digunakan untuk user yang memiliki role DOSEN")
@RestController
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


    @Operation(summary = "Verifikasi Laporan MBKM menjadi diterima")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LaporanResponse.class))),
            @ApiResponse(responseCode = "407", description = "Akses Ditolak",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/verifikasi-laporan-true/{laporanId}")
    public ResponseEntity<LaporanResponse> verifikasiLaporanTrue(
            @Parameter(description = "ID Laporan yang ingin diverifikasi", example = "123")
            @PathVariable("laporanId") Integer laporanId,
            Authentication authentication) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (laporan.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            LocalDateTime currentTime = LocalDateTime.now();
            laporan.setVerifikasi(true);
            laporan.setWaktuUpdate(Timestamp.valueOf(currentTime));
            laporanService.saveLaporan(laporan);
            return new ResponseEntity<>(new LaporanResponse(laporan), HttpStatus.OK);
        } else
            return new ResponseEntity(new MessageResponse(AKSES_DITOLAK), HttpStatus.PROXY_AUTHENTICATION_REQUIRED);

    }

    @Operation(summary = "Verifikasi Laporan MBKM menjadi ditolak")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LaporanResponse.class))),
            @ApiResponse(responseCode = "407", description = "Akses Ditolak",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/verifikasi-laporan-false/{laporanId}")
    public ResponseEntity<LaporanResponse> verifikasiLaporanFalse(
            @Parameter(description = "ID Laporan yang ingin diverifikasi", example = "123")
            @PathVariable("laporanId") Integer laporanId,
            @Parameter(description = "menambahkan catatan", example = "laporan sama seperti sebelumnya")
            @RequestParam("catatan") String catatan,
            Authentication authentication) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (laporan.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            LocalDateTime currentTime = LocalDateTime.now();
            laporan.setCatatan(catatan);
            laporan.setVerifikasi(false);
            laporan.setWaktuUpdate(Timestamp.valueOf(currentTime));
            laporanService.saveLaporan(laporan);

            return new ResponseEntity<>(new LaporanResponse(laporan), HttpStatus.OK);
        } else
            return new ResponseEntity(new MessageResponse(AKSES_DITOLAK), HttpStatus.PROXY_AUTHENTICATION_REQUIRED);


    }

    @Operation(summary = "Verifikasi Tugas Akhir MBKM menjadi di terima")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TugasAkhirResponse.class))),
            @ApiResponse(responseCode = "407", description = "Akses Ditolak",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/verifikasi-tugas-akhir-true/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirResponse> verifikasiTugasAkhirTrue(
            @Parameter(description = "ID Tugas Akhir yang ingin diverifikasi", example = "123")
            @PathVariable("tugasAkhirId") Integer tugasAkhirId,
            Authentication authentication) {
        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (ta.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            LocalDateTime currentTime = LocalDateTime.now();
            ta.setVerifikasi(true);
            ta.setWaktuUpdate(Timestamp.valueOf(currentTime));
            tugasAkhirService.saveTugasAkhir(ta);
            return new ResponseEntity<>(new TugasAkhirResponse(ta), HttpStatus.OK);
        } else
            return new ResponseEntity(new MessageResponse(AKSES_DITOLAK), HttpStatus.PROXY_AUTHENTICATION_REQUIRED);


    }

    @Operation(summary = "Verifikasi Tugas Akhir MBKM menjadi ditolak")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TugasAkhirResponse.class))),
            @ApiResponse(responseCode = "407", description = "Akses Ditolak",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/verifikasi-tugas-akhir-false/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirResponse> verifikasiTugasAkhirFalse(
            @Parameter(description = "ID Tugas Akhir yang ingin diverifikasi", example = "123")
            @PathVariable("tugasAkhirId") Integer tugasAkhirId,
            @Parameter(description = "menambahakan catatan", example = "gambar pada file laporan tidak jelas")
            @RequestParam("catatan") String catatan,
            Authentication authentication) {
        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (ta.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            LocalDateTime currentTime = LocalDateTime.now();
            ta.setCatatan(catatan);
            ta.setVerifikasi(false);
            ta.setWaktuUpdate(Timestamp.valueOf(currentTime));
            tugasAkhirService.saveTugasAkhir(ta);

            return new ResponseEntity<>(new TugasAkhirResponse(ta), HttpStatus.OK);
        } else
            return new ResponseEntity(new MessageResponse(AKSES_DITOLAK), HttpStatus.PROXY_AUTHENTICATION_REQUIRED);


    }

    @Operation(summary = "menampilkan daftar Laporan berdasarkan userId dosen ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TugasAkhirResponse.class)))
    })
    @GetMapping("/list-laporan")
    public ResponseEntity<LaporanResponse> getLaporanByUserId(
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        List<Laporan> laporan = laporanService.findLaporanByDosenId(dosen.getDosenId());
        List<LaporanResponse> taGetResponse =
                laporan.stream().map(LaporanResponse::new).collect(Collectors.toList());

        return new ResponseEntity(taGetResponse, HttpStatus.OK);
    }

    @Operation(summary = "menampilkan daftar Tugas Akhir berdasarkan userId dosen ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TugasAkhirResponse.class)))
    })
    @GetMapping("/list-tugas-akhir")
    public ResponseEntity<TugasAkhirResponse> getTugasAkhirByUserId(
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        List<TugasAkhir> ta = tugasAkhirService.getTugasAkhirByDosenId(dosen.getDosenId());
        List<TugasAkhirResponse> taGetResponse =
                ta.stream().map(TugasAkhirResponse::new).collect(Collectors.toList());

        return new ResponseEntity(taGetResponse, HttpStatus.OK);
    }

    @Operation(summary = "menampilkan detail Laporan Tugas Akhir berdasarkan tugasAkhirId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TugasAkhirResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "407", description = "Akses Ditolak",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @GetMapping("/detail-tugas-akhir/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirResponse> getDetailTugasAkhirById(
            @Parameter(description = "ID Tugas Akhir yang ingin ditampilkan", example = "123")
            @PathVariable("tugasAkhirId") Integer tugasAkhirId,
            Authentication authentication) {
        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (ta == null) {
            return new ResponseEntity(new MessageResponse("Not Found"), HttpStatus.NOT_FOUND);
        }
        if (ta.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            return new ResponseEntity<>(new TugasAkhirResponse(ta), HttpStatus.OK);
        } else
            return new ResponseEntity(new MessageResponse(AKSES_DITOLAK), HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
    }

    @Operation(summary = "menampilkan detail Laporan berdasarkan laporanId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LaporanResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "407", description = "Akses Ditolak",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @GetMapping("/detail-laporan/{laporanId}")
    public ResponseEntity<LaporanResponse> getDetailLaporanById(
            @Parameter(description = "ID Tugas Akhir yang ingin ditampilkan", example = "123")
            @PathVariable("laporanId") Integer laporanId,
            Authentication authentication) {
        Laporan laporan = laporanService.findByLaporanId(laporanId);
        Users users = usersService.findByUsername(authentication.getName());
        Dosen dosen = dosenService.getDosenByUserId(users.getUserId());
        if (laporan.getDosenId().getDosenId().equals(dosen.getDosenId())) {
            return new ResponseEntity<>(new LaporanResponse(laporan), HttpStatus.OK);
        } else
            return new ResponseEntity(new MessageResponse(AKSES_DITOLAK), HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
    }

}
