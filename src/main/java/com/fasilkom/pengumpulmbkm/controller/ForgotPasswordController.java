package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.AccountRecoveryToken;
import com.fasilkom.pengumpulmbkm.model.response.MessageResponse;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.AccountRecoveryService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import static com.fasilkom.pengumpulmbkm.model.Info.*;

@Tag(name = "2. Reset Password", description = "API untuk melakukan reset password")
@Controller
@RequestMapping("/account-recovery")
public class ForgotPasswordController {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private UsersService usersService;

    @Autowired
    private AccountRecoveryService recoveryService;

    @Operation(summary = "membuat token yang dikirimkan ke email untuk melakukan reset password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/create-token")
    public ResponseEntity<String> createRecoveryToken(
            @Parameter(description = "Email untuk mengirimkan recoverty token", example = "user@email.com")
            @RequestParam String email, Model model) {
        Users user = usersService.findByEmail(email);
        if (user != null) {
            recoveryService.createRecoveryToken(user);
            model.addAttribute("message", "Email reset password telah dikirim ke " + email);

            return new ResponseEntity(new MessageResponse(RECOVERY_SUCCESS), HttpStatus.OK);
        } else {
            return new ResponseEntity(new MessageResponse(EMAIL_NOT_FOUND), HttpStatus.NOT_FOUND);
        }
    }


    @Operation(summary = "Melakukan reset password ketika sudah mendapatkan token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/reset-password/{token}")
    public ResponseEntity<String> resetPassword(
            @Parameter(description = "Token yang berasal dari email")
            @PathVariable("token") String token,
            @Parameter(description = "masukan password baru")
            @RequestParam("password") String newPassword,
            @Parameter(description = "ulangi pengetikan password baru")
            @RequestParam("passwordRetype") String newPasswordRetype) {
        AccountRecoveryToken accountRecoveryToken = recoveryService.getRecoveryTokenByToken(token);
        if (recoveryService.validateRecoveryToken(token)){
            return new ResponseEntity(new MessageResponse("Token Expired"),HttpStatus.BAD_REQUEST);
        }
        if (accountRecoveryToken == null) {
            return new ResponseEntity(new MessageResponse("Invalid token"),HttpStatus.BAD_REQUEST);
        }
        if (newPassword.equals(newPasswordRetype)) {
            Users users = recoveryService.getRecoveryTokenByToken(token).getUser();
            users.setPassword(passwordEncoder.encode(newPassword));
            accountRecoveryToken.setToken(null);
            usersService.savePassword(users);
            recoveryService.saveToken(accountRecoveryToken);

            return new ResponseEntity(new MessageResponse("Password reset successful"),HttpStatus.OK);
        } else {
            return new ResponseEntity(PASSWORD_SAMA, HttpStatus.BAD_REQUEST);
        }


    }


}
