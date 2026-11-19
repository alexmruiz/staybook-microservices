package com.staybook.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staybook.auth.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{

    Optional<VerificationToken> findByTokenHash(String tokenHash);
    Optional<VerificationToken> findByUser_Id(Long userId);
}
