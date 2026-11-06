
package com.example.demo.config;

import com.example.demo.service.JwtService;
import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization != null
                && authorization.startsWith("Bearer ")) {

            String token = authorization.substring(7);

            try {
                Claims claims = jwtService.validarToken(token);

                String email = claims.getSubject();
                String rol = claims.get("rol", String.class);

                if (email == null || email.isBlank()
                        || !("USER".equals(rol) || "ADMIN".equals(rol))) {
                    throw new IllegalArgumentException("Token inválido");
                }

                var autoridad = new SimpleGrantedAuthority(
                        "ROLE_" + rol
                );

                var autenticacion =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(autoridad)
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(autenticacion);

            } catch (IllegalArgumentException e) {

                SecurityContextHolder.clearContext();

                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Token JWT inválido o expirado"
                );
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
