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

/**
 * Servicio de autenticación y autorización.
 * Gestiona el registro, login y consulta de usuarios.
 */
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

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request Datos de registro que contienen email, nombre y contraseña
     * @return Datos del usuario registrado (sin contraseña)
     * @throws EmailAlreadyExistsException Si el email ya está registrado
     */
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

    /**
     * Autentica un usuario con sus credenciales.
     * 
     * @param request Credenciales de login (email y contraseña)
     * @return Respuesta con token JWT, tipo de autenticación y datos del usuario
     * @throws InvalidCredentialsException Si el email no existe o la contraseña es incorrecta
     */
    public AuthResponseDto login(LoginRequestDto request) {
        try {
            // Spring Security valida las credenciales contra la base de datos
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Auth user = (Auth) userDetails;

            // Generamos el token JWT con los datos del usuario autenticado
            String jwtToken = jwtService.generateToken(userDetails);

            return new AuthResponseDto(jwtToken, "Bearer", jwtService.getJwtExpiration(), user.getEmail(),
                    user.getRole());

        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Email o contraseña incorrectos");
        }
    }

    /**
     * Obtiene los datos de un usuario a partir de su email.
     *
     * @param email Email del usuario a buscar
     * @return Datos del usuario (sin contraseña), o null si el email es null
     * @throws InvalidCredentialsException Si el email no existe en el sistema
     */
    public UserResponseDto getEmailUser(String email) {
        if (email == null) {
            return null;
        }

        Auth auth = repository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales no validas"));

        return mapper.toResponseDto(auth);
    }

}
