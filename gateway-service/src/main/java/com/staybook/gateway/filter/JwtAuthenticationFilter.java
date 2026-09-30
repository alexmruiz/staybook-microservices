package com.staybook.gateway.filter;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;

    private static final List<String> PUBLIC_PATHS = List.of("/api/auth/register", "/api/auth/login");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 1. Si es una ruta pública, dejamos pasar la petición sin comprobar token
        if (PUBLIC_PATHS.contains(path)) {
            return chain.filter(exchange);
        }

        // 2. Verificamos si viene la cabecera Authorization con formato Bearer
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        try {
            // 3. Extraemos y validamos el token JWT (firma y expiración)
            String token = authHeader.substring(7);
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token); // Si falla (expirado, firma mala), lanza JwtException

        } catch (JwtException e) {
            return unauthorized(exchange);
        }

        // 4. Si todo es correcto, permitimos que la petición continúe hacia el microservicio
        return chain.filter(exchange);
    }

    // Método auxiliar para rechazar la petición con un 401 Unauthorized de forma reactiva
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // Se ejecuta de los primeros en la cadena de filtros
    }
}