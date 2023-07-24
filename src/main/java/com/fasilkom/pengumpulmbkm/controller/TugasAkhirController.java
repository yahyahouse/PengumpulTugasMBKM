package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.response.LaporanResponse;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fasilkom.pengumpulmbkm.model.Info.AKSES_DITOLAK;

@Tag(name = "Tugas Akhir MBKM", description = "API for processing various operations with Tugas Akhir entity")
@Order(5)
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/mahasiswa/tugas-akhir")
public class TugasAkhirController {

    @Autowired
    private UsersService usersService;
    @Autowired
    private DosenService dosenService;
    @Autowired
    private ProgramService programService;
    @Autowired
    private TugasAkhirService tugasAkhirService;

    @Operation(summary = "Upload Tugas Akhir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TugasAkhirResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping(value = "/upload-tugas-akhir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TugasAkhirResponse> uploadTugasAkhir(
            @Parameter(description = "ID Dosen sesuai dengan SK", example = "123")
            @RequestParam("dosenId") Integer dosenId,
            @Parameter(description = "ID program sesuai dengan yang diikuti", example = "123")
            @RequestParam("programId") Integer prgramId,
            @Parameter(description = "File sertifikat")
            @RequestParam("sertifikat") MultipartFile sertifikat,
            @Parameter(description = "File lembar pengesahan")
            @RequestParam("lembarPengesahan") MultipartFile lembarPengesahan,
            @Parameter(description = "File nilai")
            @RequestParam("nilai") MultipartFile nilai,
            @Parameter(description = "File Laporan Tugas Akhir")
            @RequestParam("laporanTugasAkhir") MultipartFile laporanTugasAkhir,
            Authentication authentication) {
        try {
            Users user = usersService.findByUsername(authentication.getName());
            TugasAkhir ta = new TugasAkhir();
            Users users = usersService.findByUserId(user.getUserId());
            Dosen dosen = dosenService.getDosenByDosenId(dosenId);
            Program program = programService.findByProgramid(prgramId);
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
            return new ResponseEntity<>(new TugasAkhirResponse(ta), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity(new MessageResponse("Error {} "+e),HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Update Laporan Tugas Akhir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TugasAkhirResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "407", description = "Akses Ditolak",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @PutMapping(value = "/update-tugas-akhir/{tugasAkhirId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TugasAkhirResponse> updateTugasAkhir(
            @Parameter(description = "ID tugas akhir yang diupdate", example = "123")
            @PathVariable("tugasAkhirId") Integer tugasAkhirId,
            @Parameter(description = "ID program yang ingin diupdate", example = "123")
            @RequestParam("programId") Integer programId,
            @Parameter(description = "File sertifikat")
            @RequestParam("sertifikat") MultipartFile sertifikat,
            @Parameter(description = "File lembar pengesahan")
            @RequestParam("lembarPengesahan") MultipartFile lembarPengesahan,
            @Parameter(description = "File nilai")
            @RequestParam("nilai") MultipartFile nilai,
            @Parameter(description = "File laporan tugas akhir")
            @RequestParam("laporanTugasAkhir") MultipartFile laporanTugasAkhir,
            Authentication authentication
    ) {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
            Users users = usersService.findByUsername(authentication.getName());
            if (ta.getUserId().getUserId().equals(users.getUserId())) {
                if (!sertifikat.isEmpty()) {
                    ta.setSertifikat(sertifikat.getBytes());
                }
                if (!lembarPengesahan.isEmpty()) {
                    ta.setLembarPengesahan(lembarPengesahan.getBytes());
                }
                if (!nilai.isEmpty()) {
                    ta.setNilai(nilai.getBytes());
                }
                if (!laporanTugasAkhir.isEmpty()) {
                    ta.setLaporanTugasAkhir(laporanTugasAkhir.getBytes());
                }
                Program program = programService.findByProgramid(programId);
                ta.setProgramId(program);
                ta.setWaktuUpdate(Timestamp.valueOf(currentTime));
                tugasAkhirService.saveTugasAkhir(ta);
                return new ResponseEntity<>(new TugasAkhirResponse(ta), HttpStatus.OK);
            } else {
                return new ResponseEntity(new MessageResponse(AKSES_DITOLAK), HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
            }
        } catch (IOException e) {
            return new ResponseEntity(new MessageResponse("Error {} "+e),HttpStatus.BAD_REQUEST);
        }

    }

    @Operation(summary = "menampilkan detail Laporan Tugas Akhir berdasarkan tugasAkhirId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TugasAkhirResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "407", description = "Akses Ditolak",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @GetMapping("/detail-tugas-akhir/{tugasAkhirId}")
    public ResponseEntity<TugasAkhirGetDetailResponse> getTugasAkhirByid(
            @Parameter(description = "ID tugas akhir ")
            @PathVariable("tugasAkhirId") Integer tugasAkhirId,
            Authentication authentication) {
        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
        if (ta == null){
            return new ResponseEntity(new MessageResponse("Not Found"),HttpStatus.NOT_FOUND);
        }
        Users users = usersService.findByUsername(authentication.getName());
        if (ta.getUserId().getUserId().equals(users.getUserId())) {
            return new ResponseEntity<>(new TugasAkhirGetDetailResponse(ta), HttpStatus.OK);
        } else
            return new ResponseEntity(new MessageResponse(AKSES_DITOLAK), HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
    }

    @Operation(summary = "menampilkan daftar laporan sesuai season login ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TugasAkhirResponse.class))),
    })
    @GetMapping("/list-tugas-akhir")
    public ResponseEntity<TugasAkhirResponse> getTugasAkhirByUserId(
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        List<TugasAkhir> ta = tugasAkhirService.getTugasAkhirByUserId(users.getUserId());
        List<TugasAkhirResponse> taGetResponse =
                ta.stream().map(TugasAkhirResponse::new).collect(Collectors.toList());

        return new ResponseEntity(taGetResponse, HttpStatus.OK);
    }

//    @Operation(summary = "menampilkan file sertifikat")
//    @GetMapping("/sertifikat/{tugasAkhirId}")
//    public ResponseEntity<Resource> displayFileSertifikat(
//            @PathVariable Integer tugasAkhirId,
//            Authentication authentication) {
//        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
//        Users users = usersService.findByUsername(authentication.getName());
//        if (ta.getUserId().getUserId().equals(users.getUserId())) {
//            Optional<TugasAkhir> optionalDocument = Optional.ofNullable(tugasAkhirService.findByTugasAkhirId(tugasAkhirId));
//            if (optionalDocument.isPresent()) {
//                TugasAkhir tugasAkhir = optionalDocument.get();
//                byte[] fileBytes = tugasAkhir.getSertifikat();
//
//                ByteArrayResource resource = new ByteArrayResource(fileBytes);
//                HttpHeaders headers = new HttpHeaders();
//                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sertifikat.pdf");
//
//                return ResponseEntity.ok()
//                        .headers(headers)
//                        .contentLength(fileBytes.length)
//                        .contentType(MediaType.APPLICATION_PDF)
//                        .body(resource);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } else {
//            return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
//        }
//
//    }
//
//    @Operation(summary = "menampilkan file nilai")
//    @GetMapping("/nilai/{tugasAkhirId}")
//    public ResponseEntity<Resource> displayFileNilai(
//            @PathVariable Integer tugasAkhirId,
//            Authentication authentication) {
//        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
//        Users users = usersService.findByUsername(authentication.getName());
//        if (ta.getUserId().getUserId().equals(users.getUserId())) {
//            Optional<TugasAkhir> optionalDocument = Optional.ofNullable(tugasAkhirService.findByTugasAkhirId(tugasAkhirId));
//            if (optionalDocument.isPresent()) {
//                TugasAkhir tugasAkhir = optionalDocument.get();
//                byte[] fileBytes = tugasAkhir.getNilai();
//
//                ByteArrayResource resource = new ByteArrayResource(fileBytes);
//                HttpHeaders headers = new HttpHeaders();
//                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=nilai.pdf");
//
//                return ResponseEntity.ok()
//                        .headers(headers)
//                        .contentLength(fileBytes.length)
//                        .contentType(MediaType.APPLICATION_PDF)
//                        .body(resource);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } else {
//            return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
//        }
//
//    }
//
//    @Operation(summary = "menampilkan file Lembar Pengesahan")
//    @GetMapping("/lembar-pengesahan/{tugasAkhirId}")
//    public ResponseEntity<Resource> displayFileLembarPengesahan(
//            @PathVariable Integer tugasAkhirId,
//            Authentication authentication) {
//        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
//        Users users = usersService.findByUsername(authentication.getName());
//        if (ta.getUserId().getUserId().equals(users.getUserId())) {
//            Optional<TugasAkhir> optionalDocument = Optional.ofNullable(tugasAkhirService.findByTugasAkhirId(tugasAkhirId));
//            if (optionalDocument.isPresent()) {
//                TugasAkhir tugasAkhir = optionalDocument.get();
//                byte[] fileBytes = tugasAkhir.getLembarPengesahan();
//
//                ByteArrayResource resource = new ByteArrayResource(fileBytes);
//                HttpHeaders headers = new HttpHeaders();
//                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=lembarPengesahan.pdf");
//
//                return ResponseEntity.ok()
//                        .headers(headers)
//                        .contentLength(fileBytes.length)
//                        .contentType(MediaType.APPLICATION_PDF)
//                        .body(resource);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } else {
//            return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
//        }
//
//    }
//
//    @Operation(summary = "menampilkan file Laporan Tugas Akhir")
//    @GetMapping("/laporan-tugas-akhir/{tugasAkhirId}")
//    public ResponseEntity<Resource> displayFileTugasAkhir(
//            @PathVariable Integer tugasAkhirId,
//            Authentication authentication) {
//        TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
//        Users users = usersService.findByUsername(authentication.getName());
//        if (ta.getUserId().getUserId().equals(users.getUserId())) {
//            Optional<TugasAkhir> optionalDocument = Optional.ofNullable(tugasAkhirService.findByTugasAkhirId(tugasAkhirId));
//            if (optionalDocument.isPresent()) {
//                TugasAkhir tugasAkhir = optionalDocument.get();
//                byte[] fileBytes = tugasAkhir.getLaporanTugasAkhir();
//
//                ByteArrayResource resource = new ByteArrayResource(fileBytes);
//                HttpHeaders headers = new HttpHeaders();
//                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=TugasAkhir.pdf");
//
//                return ResponseEntity.ok()
//                        .headers(headers)
//                        .contentLength(fileBytes.length)
//                        .contentType(MediaType.APPLICATION_PDF)
//                        .body(resource);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } else {
//            return new ResponseEntity(AKSES_DITOLAK, HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
//        }
//    }
}


