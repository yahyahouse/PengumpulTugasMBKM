package com.fasilkom.pengumpulmbkm.controller;

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
import com.sun.org.glassfish.gmbal.NameValue;
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
import org.springframework.web.multipart.MultipartFile;


import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasilkom.pengumpulmbkm.model.Info.AKSES_DITOLAK;

@Tag(name = "5. Tugas Akhir MBKM",
        description = "API yang digunakan oleh role MAHASISWA untuk dapat melakukan CRUD pada entity Tugas Akhir")
@RestController
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
    private static final Logger LOG = LoggerFactory.getLogger(LaporanController.class);

    @Operation(summary = "Melakukan Upload Tugas Akhir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TugasAkhirResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping(value = "/upload-tugas-akhir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TugasAkhirResponse> uploadTugasAkhir(
            @Parameter(description = "ID Dosen sesuai dengan SK", example = "123")
            @NotBlank(message = "dosenId cannot be null")
            @RequestParam("dosenId") Integer dosenId,
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
                return new ResponseEntity(new MessageResponse("Dosen Not Found"), HttpStatus.NOT_FOUND);
            }
            if (program == null) {
                return new ResponseEntity(new MessageResponse("Program MBKM Not Found"), HttpStatus.NOT_FOUND);
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
            return new ResponseEntity<>(new TugasAkhirResponse(ta), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity(new MessageResponse("Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Melakukan Update Laporan Tugas Akhir")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TugasAkhirResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "407", description = "Akses Ditolak",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
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
            @RequestParam(value = "sertifikat",required = false) MultipartFile sertifikat,
            @Parameter(description = "File lembar pengesahan")
            @RequestParam(value = "lembarPengesahan",required = false) MultipartFile lembarPengesahan,
            @Parameter(description = "File nilai")
            @RequestParam(value = "nilai",required = false) MultipartFile nilai,
            @Parameter(description = "File laporan tugas akhir")
            @RequestParam(value = "laporanTugasAkhir",required = false) MultipartFile laporanTugasAkhir,
            Authentication authentication
    ) {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            TugasAkhir ta = tugasAkhirService.findByTugasAkhirId(tugasAkhirId);
            Program program = programService.findByProgramid(programId);
            if (ta == null){
                return new ResponseEntity(new MessageResponse(" Tugas Akhir Not Found"), HttpStatus.NOT_FOUND);
            }
            if (program==null){
                return new ResponseEntity(new MessageResponse(" Program MBKM Not Found"), HttpStatus.NOT_FOUND);
            }
            Users users = usersService.findByUsername(authentication.getName());
            if (ta.getUserId().getUserId().equals(users.getUserId())) {
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
                return new ResponseEntity<>(new TugasAkhirResponse(ta), HttpStatus.OK);
            } else {
                return new ResponseEntity(new MessageResponse(AKSES_DITOLAK), HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
            }
        } catch (IOException e) {
            return new ResponseEntity(new MessageResponse("Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
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
        if (ta == null) {
            return new ResponseEntity(new MessageResponse("Not Found"), HttpStatus.NOT_FOUND);
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


