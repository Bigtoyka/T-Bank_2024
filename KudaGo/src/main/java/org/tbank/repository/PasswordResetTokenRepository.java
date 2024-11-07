package org.tbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tbank.models.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}
