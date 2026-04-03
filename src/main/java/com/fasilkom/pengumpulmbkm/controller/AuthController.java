package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.util.CommonConstant;
import com.fasilkom.pengumpulmbkm.model.JwtResponse;
import com.fasilkom.pengumpulmbkm.model.SignupRequest;
import com.fasilkom.pengumpulmbkm.model.response.BaseResponse;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.service.AuthService;
import com.fasilkom.pengumpulmbkm.util.ResponseUtil;
import com.yahya.commonlogger.StructuredLogger;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Map;

@Tag(name = "1. Auth", description = "API yang digunakan untuk dapat mengakses sistem dengan mendaftarkan akun baru atau login")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final StructuredLogger structuredLogger;

    private final AuthService authService;

    @Operation(summary = "Login untuk dapat mengakses sisetem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = CommonConstant.NOT_FOUND,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> authenticateUser(
            @Schema(example = "{" +
                    "\"email\":\"userTest@gmail.com\"," +
                    "\"password\":\"userTest\"" +
                    "}")
            @RequestBody Map<String, Object> login) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(login);

            structuredLogger.newLog()
                    .withLogLevel(LogLevel.INFO)
                    .onSuccess(jwtResponse, 0);

            return ResponseUtil.ok(jwtResponse);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (BadCredentialsException e) {
            structuredLogger.newLog()
                    .withLogLevel(LogLevel.ERROR)
                    .onFailure(e, 0);
            return ResponseUtil.error(HttpStatus.UNAUTHORIZED, "Invalid password");
        } catch (Exception e) {
            structuredLogger.newLog()
                    .withLogLevel(LogLevel.ERROR)
                    .onFailure(e, 0);
            return ResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @Operation(summary = "Registrasi akun baru")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<BaseResponse> registerUser(
            @Valid
            @Schema(example = "{" +
                    "\"username\":\"userTest1234\"," +
                    "\"email\":\"userTest1234@gmail.com\"," +
                    "\"namaLengkap\":\"userTestLengkap\"," +
                    "\"password\":\"userTest1234\"," +
                    "\"npm\":\"1234567891011\"," +
                    "\"role\":[\"DOSEN\", \"MAHASISWA\", \"ADMIN\"]," +
                    "\"prodi\":[\"TI\"]" +
                    "}")
            @RequestBody SignupRequest signupRequest) {
        try {
            MessageResponse response = authService.registerUser(signupRequest);

            structuredLogger.newLog()
                    .withLogLevel(LogLevel.INFO)
                    .onSuccess(response, 0);

            return ResponseUtil.ok(response.getMessage(), null);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            structuredLogger.newLog()
                    .withLogLevel(LogLevel.ERROR)
                    .onFailure(e, 0);
            return ResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e.getMessage());
        }
    }
}
