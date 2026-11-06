
package com.example.demo.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtService(
        @Value("${app.jwt.secret}") String secreto,
        @Value("${app.jwt.expiration-ms}") long expiracionMs) {

    this.clave = Keys.hmacShaKeyFor(
            secreto.getBytes(StandardCharsets.UTF_8)
    );

    this.expiracionMs = expiracionMs;
}

    public String generarToken(Long usuarioId, String email, String rol) {

        Date ahora = new Date();

        Date vencimiento = new Date(
                ahora.getTime() + expiracionMs
        );

        return Jwts.builder()
                .subject(email)
                .claim("usuarioId", usuarioId)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(vencimiento)
                .signWith(clave)
                .compact();
    }
    
public Claims validarToken(String token) {

    try {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    } catch (JwtException | IllegalArgumentException e) {
        throw new IllegalArgumentException(
                "Token JWT inválido o expirado",
                e
        );
    }
}

public String obtenerEmail(String token) {
    return validarToken(token).getSubject();
}

public String obtenerRol(String token) {
    return validarToken(token).get("rol", String.class);
}

public Long obtenerUsuarioId(String token) {
    return validarToken(token)
            .get("usuarioId", Number.class)
            .longValue();
}

}
