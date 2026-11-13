package com.staybook.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.HexFormat;

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
import com.staybook.auth.dto.request.VerifyEmailRequest;
import com.staybook.auth.dto.response.AuthResponseDto;
import com.staybook.auth.dto.response.UserBookingResponseDto;
import com.staybook.auth.dto.response.UserResponseDto;
import com.staybook.auth.entity.Auth;
import com.staybook.auth.entity.VerificationToken;
import com.staybook.auth.enums.TypeRole;
import com.staybook.auth.exception.EmailAlreadyExistsException;
import com.staybook.auth.exception.ExpiredTokenException;
import com.staybook.auth.exception.IdNotFoundException;
import com.staybook.auth.exception.InvalidCredentialsException;
import com.staybook.auth.exception.InvalidTokenException;
import com.staybook.auth.mapper.AuthMapper;
import com.staybook.auth.repository.AuthRepository;
import com.staybook.auth.repository.VerificationTokenRepository;
import com.staybook.auth.security.JwtService;

/**
 * Servicio de autenticación y autorización. Gestiona el registro, login y
 * consulta de usuarios.
 */
@Service
public class AuthService {

    private final AuthRepository repository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final SecureRandom sr = new SecureRandom();

    public AuthService(AuthRepository repository, AuthMapper mapper, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService,
            VerificationTokenRepository verificationTokenRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.verificationTokenRepository = verificationTokenRepository;
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
        String rawToken = createVerificationToken(saved);

        return mapper.toResponseDto(saved);
    }

    /**
     * Autentica un usuario con sus credenciales.
     * 
     * @param request Credenciales de login (email y contraseña)
     * @return Respuesta con token JWT, tipo de autenticación y datos del usuario
     * @throws InvalidCredentialsException Si el email no existe o la contraseña es
     *                                     incorrecta
     */
    public AuthResponseDto login(LoginRequestDto request) {
        try {
            // Spring Security valida las credenciales contra la base de datos
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Auth user = (Auth) userDetails;

            // Generamos el token JWT con los datos del usuario autenticado
            String jwtToken = jwtService.generateToken(userDetails, user.getId());

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

    /**
     * Actualiza el perfil de un usuario existente.
     *
     * @param email      Email del usuario a actualizar
     * @param requestDto Nuevos datos del perfil (email, nombre, apellido,
     *                   contraseña)
     * @return Datos del usuario actualizado
     * @throws InvalidCredentialsException Si el email no existe en el sistema
     */
    @Transactional
    public UserResponseDto updateUserProfile(String email, RegisterRequestDto requestDto) {
        if (email == null) {
            return null;
        }

        Auth auth = repository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales no validas"));

        auth.setName(requestDto.name());
        auth.setSurname(requestDto.surname());
        auth.setEmail(requestDto.email());
        auth.setPassword(passwordEncoder.encode(requestDto.password()));
        auth.setRole(TypeRole.ROLE_USER);

        Auth result = repository.save(auth);

        return mapper.toResponseDto(result);
    }

    public UserBookingResponseDto getUserById(Long userId) {

        Auth auth = repository.findById(userId).orElseThrow(() -> new IdNotFoundException("Id no encontrado"));

        return new UserBookingResponseDto(auth.getId(), auth.getEmail(), auth.getName(), auth.getSurname());
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        this.sr.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no está disponible", e);
        }
    }

    private String createVerificationToken(Auth user) {
        String rawToken = generateRawToken();
        String tokenHash = hashToken(rawToken);

        VerificationToken token = verificationTokenRepository.findByUser_Id(user.getId())
                .orElseGet(VerificationToken::new);

        token.setUser(user);
        token.setTokenHash(tokenHash);
        token.setExpiresAt(LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(30));
        token.setUsedAt(null);

        verificationTokenRepository.save(token);

        return rawToken;
    }

    @Transactional 
    public boolean verifyEmail(VerifyEmailRequest hash) {
        VerificationToken token = verificationTokenRepository.findByTokenHash(hash.token())
                .orElseThrow(() -> new InvalidTokenException("Token invalido, no se pudo verificar"));

        if (token.getExpiresAt().isAfter(LocalDateTime.now(Clock.systemDefaultZone()))) {
            throw new ExpiredTokenException("Token expirado");
        }

        if (token.getUsedAt() != null) {
            throw new InvalidTokenException("El token ya ha sido verificado");
        }

        token.setUsedAt(LocalDateTime.now(ZoneId.systemDefault()));
        token.getUser().setEmailVerified(true);
        return true;
    }

}
