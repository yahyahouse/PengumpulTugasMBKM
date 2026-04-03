package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.util.CommonConstant;
import com.fasilkom.pengumpulmbkm.model.response.BaseResponse;
import com.fasilkom.pengumpulmbkm.model.response.DosenResponse;
import com.fasilkom.pengumpulmbkm.model.response.UsersResponse;
import com.fasilkom.pengumpulmbkm.model.users.Dosen;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.DosenService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Size;
import java.util.List;

import static com.fasilkom.pengumpulmbkm.util.CommonConstant.*;

@Tag(name = "3. Users", description = "API yang dapat digunakan oleh akun yang memiliki role ADMIN, MAHASISWA, DOSEN")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UsersController {

    private final PasswordEncoder passwordEncoder;

    private final UsersService usersService;

    private final DosenService dosenService;

    @Operation(summary = "Mendapatkan detail profil untuk user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
    })
    @GetMapping(value = "/users/me")
    public ResponseEntity<BaseResponse> getDetailUser(
            Authentication authentication) {
        Users users = usersService.findByUsername(authentication.getName());
        return ResponseUtil.ok(new UsersResponse(users));
    }

    @Operation(summary = "melakukan update password untuk user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
    })
    @PutMapping("/users/me/password")
    public ResponseEntity<BaseResponse> updateUsersPassword(
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
                return ResponseUtil.ok(PASSWORD_TERGANTI, null);
            } else
                return ResponseUtil.error(HttpStatus.BAD_REQUEST, SALAH_PASSWORD);

        } else
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, PASSWORD_SAMA);
    }


    @Operation(summary = "melakukan update profile untuk user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
    })
    @PutMapping("/users/me")
    public ResponseEntity<BaseResponse> updateUsersProfile(
            @Parameter(description = "masukan nomor handphone", example = "082009296186")
            @RequestParam("noHp") String noHp,
            @Parameter(description = "masukan nama lengkap", example = "Lena VonRueden")
            @RequestParam("namaLengkap") String namalengkap,
            @Parameter(description = "masukan nomor pokok mahasiswa (NPM)", example = "1910631170000")
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
            return ResponseUtil.ok(UPDATE_BERHASIL, null);
        } else
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, SALAH_PASSWORD);

    }

    @Operation(summary = "Mendapatkan semua daftar dosen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping(value = "/lecturers")
    public ResponseEntity<BaseResponse> getAllDosen() {
        List<Dosen> dosen = dosenService.getAllDosen();
        List<DosenResponse> allDosen =
                dosen.stream().map(DosenResponse::new).toList();
        return ResponseUtil.ok(allDosen);
    }

    @Operation(summary = "mendapatkan detail dosen sesuai dengan id dosen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = CommonConstant.NOT_FOUND,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
    })
    @GetMapping(value = "/lecturers/{dosenId}")
    public ResponseEntity<BaseResponse> getDetailDosenByDosenId(
            @PathVariable @Parameter(description = "ID dosen untuk menampilkan detail", example = "123") String dosenId) {
        Dosen dosen = dosenService.getDosenByDosenId(dosenId);
        if (dosen == null) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, CommonConstant.NOT_FOUND);
        }
        return ResponseUtil.ok(new DosenResponse(dosen));
    }
}
