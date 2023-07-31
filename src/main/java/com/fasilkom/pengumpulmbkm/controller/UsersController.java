package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.response.DosenResponse;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.model.response.UsersResponse;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasilkom.pengumpulmbkm.model.Info.*;

@Tag(name = "3. Users", description = "API yang dapat digunakan oleh akun yang memiliki role ADMIN, MAHASISWA, DOSEN")
@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private UsersService usersService;
    @Autowired
    private DosenService dosenService;

    @Operation(summary = "Mendapatkan detail profil untuk user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UsersResponse.class))),
    })
    @GetMapping(value = "/profil")
    public ResponseEntity<UsersResponse> getDetailUser(
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        return new ResponseEntity<>(new UsersResponse(users), HttpStatus.OK);
    }

    @Operation(summary = "melakukan update password untuk user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
    })
    @PostMapping("/update-users-password")
    public ResponseEntity<ResponseEntity> updateUsersPassword(
            @Parameter(description = "Password lama")
            @RequestParam("old_password") String oldPassword,
            @Parameter(description = "Masukan Password baru")
            @RequestParam("password") String password,
            @Parameter(description = "Ulangi password baru")
            @RequestParam("retype_password") String retypePassword,
            Authentication authentication) {
        Users user = usersService.findByUsername(authentication.getName());
        Users users = usersService.findByUserId(user.getUserId());
        if (password.equals(retypePassword)) {
            if (passwordEncoder.matches(oldPassword, users.getPassword())) {
                usersService.updateUsersPassword(password, user.getUserId());
                return new ResponseEntity(new MessageResponse(PASSWORD_TERGANTI), HttpStatus.OK);
            } else
                return new ResponseEntity(new MessageResponse(SALAH_PASSWORD), HttpStatus.BAD_REQUEST);

        } else
            return new ResponseEntity(new MessageResponse(PASSWORD_SAMA), HttpStatus.BAD_REQUEST);
    }


    @Operation(summary = "melakukan update profile untuk user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
    })
    @PostMapping("/update-users-profile")
    public ResponseEntity updateUsersProfile(
            @Parameter(description = "masukan nomor handphone",example = "082009296186")
            @RequestParam("noHp") String noHp,
            @Parameter(description = "masukan nama lengkap",example = "Lena VonRueden")
            @RequestParam("namaLengkap") String namalengkap,
            @Parameter(description = "masukan nomor pokok mahasiswa (NPM)",example = "1910631170000")
            @Size(min = 12)
            @RequestParam("npm") String npm,
            @Parameter(description = "masukan password")
            @RequestParam("password") String password,
            Authentication authentication) {
        Users user = usersService.findByUsername(authentication.getName());
        Users users = usersService.findByUserId(user.getUserId());
        if (passwordEncoder.matches(password, users.getPassword())) {
            users.setNamaLengkap(namalengkap);
            users.setNoHp(noHp);
            users.setNpm(npm);
            usersService.updateProfile(users);
            return new ResponseEntity(new MessageResponse(UPDATE_BERHASIL), HttpStatus.OK);
        } else
            return new ResponseEntity(new MessageResponse(SALAH_PASSWORD), HttpStatus.BAD_REQUEST);

    }

    @Operation(summary = "Mendapatkan semua daftar dosen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DosenResponse.class)))
    })
    @GetMapping(value = "/all-dosen")
    public ResponseEntity<List<DosenResponse>> getAllDosen() {
        List<Dosen> dosen = dosenService.getAllDosen();
        List<DosenResponse> allDosen =
                dosen.stream().map(DosenResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(allDosen, HttpStatus.OK);
    }

    @Operation(summary = "mendapatkan detail dosen sesuai dengan id dosen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DosenResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
    })
    @GetMapping(value = "/detail-profil-dosen/{dosenId}")
    public ResponseEntity<DosenResponse> getDetailDosenByDosenId(
            @Parameter(description = "ID dosen untuk menampilkan detail",example = "123")
            @PathVariable("dosenId") Integer dosenId) {
        Dosen dosen = dosenService.getDosenByDosenId(dosenId);
        if (dosen == null) {
            return new ResponseEntity(new MessageResponse("Not Found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new DosenResponse(dosen), HttpStatus.OK);
    }
}
