package com.fasilkom.pengumpulmbkm.controller;

import com.fasilkom.pengumpulmbkm.model.AccountRecoveryToken;
import com.fasilkom.pengumpulmbkm.model.users.Users;
import com.fasilkom.pengumpulmbkm.service.AccountRecoveryService;
import com.fasilkom.pengumpulmbkm.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.fasilkom.pengumpulmbkm.model.Info.PASSWORD_SAMA;

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
    @PostMapping("/create-token")
    public ResponseEntity<String> createRecoveryToken(@RequestParam String email, Model model) {
        Users user = usersService.findByEmail(email);
        if (user != null) {
            recoveryService.createRecoveryToken(user);
            model.addAttribute("message", "Email reset password telah dikirim ke " + email);

            return ResponseEntity.ok("Recovery token created successfully. please check your email");
        } else {
            return new ResponseEntity("email not found",HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/validate-token/{token}")
    public ResponseEntity<String> validateRecoveryToken(@PathVariable String token) {
        AccountRecoveryToken recoveryToken = recoveryService.getRecoveryTokenByToken(token);
        if (recoveryToken != null && recoveryToken.getExpirationDate().isAfter(LocalDateTime.now())) {
            return ResponseEntity.ok("Recovery token is valid.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired recovery token.");
        }
    }
    @Operation(summary = "Melakukan reset password ketika sudah mendapatkan token")
    @PostMapping("/reset-password/{token}")
    public ResponseEntity<String> resetPassword(@PathVariable("token") String token,
                                                @RequestParam("password") String newPassword,
                                                @RequestParam("passwordRetype") String newPasswordRetype) {
        AccountRecoveryToken accountRecoveryToken = recoveryService.getRecoveryTokenByToken(token);

        if (accountRecoveryToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }
        if (newPassword.equals(newPasswordRetype)){
            Users users = recoveryService.getRecoveryTokenByToken(token).getUser();
            users.setPassword(passwordEncoder.encode(newPassword));
            accountRecoveryToken.setToken(null);
            usersService.savePassword(users);
            recoveryService.saveToken(accountRecoveryToken);

            return ResponseEntity.ok("Password reset successful");
        }else {
            return new ResponseEntity(PASSWORD_SAMA, HttpStatus.BAD_REQUEST);
        }


    }



}
