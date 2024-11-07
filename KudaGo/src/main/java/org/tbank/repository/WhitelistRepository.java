package org.tbank.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tbank.models.Whitelist;

import java.util.Optional;

public interface WhitelistRepository extends JpaRepository<Whitelist, Long> {
    Optional<Whitelist> findByToken(String token);
    @Modifying
    @Transactional
    @Query("DELETE FROM Whitelist w WHERE w.token = :token")
    void deleteByToken(@Param("token") String token);
}
