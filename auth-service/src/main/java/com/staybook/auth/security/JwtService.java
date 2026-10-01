package com.staybook.auth.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value ("${jwt.secret}")
    private String secretKey;

    @Value ("${jwt.expiration}")
    private long jwtExpiration;
    
    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Método principal para generar el token JWT con los claims mínimos requeridos (sub, role, exp)
    public String generateToken(UserDetails userDetails, Long userId) {
        // Obtenemos el rol del usuario (ej. ROLE_ADMIN o ROLE_USER)
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority())
                .orElse("ROLE_USER");

        return Jwts.builder()
            .setSubject(userDetails.getUsername()) // 'sub': Identificador del usuario (email)
            .claim("role", role)                // 'role': Claim personalizado pedido en la tarea
            .claim("userId", userId)
            .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de creación
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // 'exp': Expiración
            .signWith(getSignInKey())           // Firma digital con nuestra clave secreta
            .compact();
    }

    public long getJwtExpiration() {
        return jwtExpiration;
    }
}
