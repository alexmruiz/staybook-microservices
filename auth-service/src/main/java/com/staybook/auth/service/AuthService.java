package com.staybook.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staybook.auth.dto.request.LoginRequestDto;
import com.staybook.auth.dto.request.RegisterRequestDto;
import com.staybook.auth.dto.response.AuthResponseDto;
import com.staybook.auth.dto.response.UserResponseDto;
import com.staybook.auth.entity.Auth;
import com.staybook.auth.enums.TypeRole;
import com.staybook.auth.exception.EmailAlreadyExistsException;
import com.staybook.auth.exception.InvalidCredentialsException;
import com.staybook.auth.mapper.AuthMapper;
import com.staybook.auth.repository.AuthRepository;
import com.staybook.auth.security.JwtService;

@Service
public class AuthService {

    private final AuthRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthRepository repository, AuthMapper mapper, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponseDto register(RegisterRequestDto request) {
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

    public AuthResponseDto login(LoginRequestDto request) {
        // 1. Spring Security autentica las credenciales
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            // 2. Si pasa, obtenemos los detalles del usuario autenticado
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            Auth user = (Auth) userDetails;

            // 4. Generamos el token JWT usando nuestro JwtService
            String jwtToken = jwtService.generateToken(userDetails);

            // 5. Retornamos la respuesta con el token, tipo y expiración
            return new AuthResponseDto(jwtToken, "Bearer", jwtService.getJwtExpiration(), user.getEmail(), user.getRole());

        } catch (AuthenticationException e) {
            // Si el email no existe o la contraseña es incorrecta, lanzamos la excepción
            // 401
            throw new InvalidCredentialsException("Email o contraseña incorrectos");
        }
    }

}
