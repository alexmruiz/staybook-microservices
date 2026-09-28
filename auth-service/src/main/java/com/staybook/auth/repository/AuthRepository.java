package com.staybook.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staybook.auth.entity.Auth;

public interface AuthRepository extends JpaRepository<Auth, Long>{
    
    Optional<Auth> findByEmail();
}
