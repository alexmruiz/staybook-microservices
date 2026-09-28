package com.staybook.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staybook.auth.dto.request.RegisterRequestDto;
import com.staybook.auth.dto.response.AuthResponseDto;
import com.staybook.auth.entity.Auth;
import com.staybook.auth.enums.TypeRole;
import com.staybook.auth.exception.EmailAlreadyExistsException;
import com.staybook.auth.mapper.AuthMapper;
import com.staybook.auth.repository.AuthRepository;

@Service
public class AuthService {

    private final AuthRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper mapper;

    public AuthService(AuthRepository repository, AuthMapper mapper, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        boolean existEmail = repository.findByEmail(request.email()).isPresent();

        if (existEmail) {
            throw new EmailAlreadyExistsException("El email ya está registrado en el sistema");
        }

        Auth user = mapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(TypeRole.ROLE_USER);

        Auth saved = repository.save(user);

        return mapper.toResponseDto(saved);
    }

}
