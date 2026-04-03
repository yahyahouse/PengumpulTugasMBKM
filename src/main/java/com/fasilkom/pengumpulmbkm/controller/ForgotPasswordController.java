package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.util.CommonConstant;
import com.fasilkom.pengumpulmbkm.model.AccountRecoveryToken;
import com.fasilkom.pengumpulmbkm.model.response.BaseResponse;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.AccountRecoveryService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import static com.fasilkom.pengumpulmbkm.util.CommonConstant.*;

@Tag(name = "2. Reset Password", description = "API untuk melakukan reset password")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/password")
public class ForgotPasswordController {


    private final PasswordEncoder passwordEncoder;

    private final UsersService usersService;

    private final AccountRecoveryService recoveryService;

    @Operation(summary = "membuat token yang dikirimkan ke email untuk melakukan reset password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = CommonConstant.NOT_FOUND,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/forgot")
    public ResponseEntity<BaseResponse> createRecoveryToken(
            @Parameter(description = "Email untuk mengirimkan recoverty token", example = "user@email.com")
            @RequestParam String email, Model model) {
        Users user = usersService.findByEmail(email);
        if (user != null) {
            recoveryService.createRecoveryToken(user);
            model.addAttribute("message", "Email reset password telah dikirim ke " + email);

            return ResponseUtil.ok(RECOVERY_SUCCESS, null);
        } else {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, EMAIL_NOT_FOUND);
        }
    }


    @Operation(summary = "Melakukan reset password ketika sudah mendapatkan token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/reset/{token}")
    public ResponseEntity<BaseResponse> resetPassword(
            @Parameter(description = "Token yang berasal dari email")
            @PathVariable("token") String token,
            @Parameter(description = "masukan password baru")
            @RequestParam("password") String newPassword,
            @Parameter(description = "ulangi pengetikan password baru")
            @RequestParam("passwordRetype") String newPasswordRetype) {
        AccountRecoveryToken accountRecoveryToken = recoveryService.getRecoveryTokenByToken(token);
        if (accountRecoveryToken == null) {
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, "Invalid token");
        }
        if (recoveryService.validateRecoveryToken(token)) {
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, "Token Expired");
        }
        if (newPassword.equals(newPasswordRetype)) {
            Users users = recoveryService.getRecoveryTokenByToken(token).getUser();
            users.setPassword(passwordEncoder.encode(newPassword));
            accountRecoveryToken.setToken(null);
            usersService.savePassword(users);
            recoveryService.saveToken(accountRecoveryToken);

            return ResponseUtil.ok("Password reset successful", null);
        } else {
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, PASSWORD_SAMA);
        }


    }


}
