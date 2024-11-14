package org.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.tbank.models.PasswordResetToken;
import org.tbank.models.User;
import org.tbank.models.UserLoginRequest;
import org.tbank.models.Whitelist;
import org.tbank.repository.PasswordResetTokenRepository;
import org.tbank.repository.UserRepository;
import org.tbank.repository.WhitelistRepository;
import org.tbank.util.JwtService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final WhitelistRepository whitelistRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, WhitelistRepository whitelistRepository, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.whitelistRepository = whitelistRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public User registerUser(UserLoginRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER);
        return userRepository.save(user);
    }

    public String authenticateUser(UserLoginRequest request, boolean rememberMe) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtService.generateToken((UserDetails) authentication.getPrincipal(), rememberMe);
            Whitelist whitelistToken = new Whitelist();
            whitelistToken.setToken(jwtToken);
            whitelistRepository.save(whitelistToken);

            return jwtToken;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка входа: " + e.getMessage());
        }
    }

    public void logout(String token) {
        log.info("Токен " + token.substring(7));
        whitelistRepository.deleteByToken(token.substring(7));
        log.info("Токен удален" + token);
    }

    public boolean isTokenValid(String token) {
        return whitelistRepository.findByToken(token).isPresent();
    }

    public String initiatePasswordReset(String username) {
        log.info("Пользователь, который хочет сбросить пароль " + username);
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            String resetToken = UUID.randomUUID().toString();

            PasswordResetToken passwordResetToken = new PasswordResetToken(null, resetToken, user, LocalDateTime.now());
            passwordResetTokenRepository.save(passwordResetToken);
            log.info("Отправлен токен для сброса пароля для  " + username);
            return resetToken;
        } else {

            throw new RuntimeException("Пользователь с таким именем не найден.");
        }
    }

    public void resetPassword(String resetToken, String verificationCode, String newPassword) {
        if (!"0000".equals(verificationCode)) {
            throw new RuntimeException("Неверный код подтверждения.");
        }
        log.info("Токен" + resetToken);
        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByToken(resetToken);
        if (optionalToken.isEmpty()) {
            throw new RuntimeException("Неверный токен сброса пароля.");
        }

        PasswordResetToken passwordResetToken = optionalToken.get();

        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new RuntimeException("Токен сброса пароля истек.");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(passwordResetToken);
    }
}
