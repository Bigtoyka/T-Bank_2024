package org.tbank.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.tbank.models.UserLoginRequest;
import org.tbank.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;


    public AuthController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserLoginRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok("Пользователь зарегистрирован успешно");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest request, @RequestParam boolean rememberMe) {
        try {
            String jwtToken = userService.authenticateUser(request, rememberMe);
            return ResponseEntity.ok(jwtToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка входа: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        SecurityContextHolder.clearContext();
        userService.logout(token);
        return ResponseEntity.ok("Пользователь разлогинелся успешно!");
    }

    @GetMapping("/status")
    public ResponseEntity<String> checkStatus(@RequestHeader("Authorization") String token) {
        if (userService.isTokenValid(token.substring(7))) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return ResponseEntity.ok("Пользователь вошел в систему как: " + authentication.getName());
            }
        }
        return ResponseEntity.ok("Пользователь не авторизирован.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String resetToken = userService.initiatePasswordReset(username);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Для сброса пароля используйте код подтверждения 0000.");
            response.put("resetToken", resetToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Ошибка сброса пароля: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка сброса пароля: " + e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String resetToken = request.get("token");
            String verificationCode = request.get("confirmationCode");
            String newPassword = request.get("newPassword");
            log.info("token: " + resetToken);
            userService.resetPassword(resetToken, verificationCode, newPassword);

            return ResponseEntity.ok("Пароль успешно сброшен!");
        } catch (Exception e) {
            log.error("Ошибка сброса пароля: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка сброса пароля: " + e.getMessage());
        }
    }
}
