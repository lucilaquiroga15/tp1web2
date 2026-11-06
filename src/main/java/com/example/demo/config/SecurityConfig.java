
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
}

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            // API REST: no usamos sesiones ni formularios
            .csrf(csrf -> csrf.disable())

            // Cada request deberá autenticarse con JWT
            .sessionManagement(session -> session
                .sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .exceptionHandling(ex -> ex
    .authenticationEntryPoint((request, response, authException) ->
        response.sendError(
            HttpServletResponse.SC_UNAUTHORIZED,
            "No autenticado"
        )
    )
)
            // Reglas de acceso
            .authorizeHttpRequests(auth -> auth

                // Registro y login públicos
                .requestMatchers("/api/auth/**")
                .permitAll()

                // Catálogo de productos público
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/productos/**"
                )
                .permitAll()

                // Endpoint de estado público
                .requestMatchers("/health")
                .permitAll()

                                // Swagger público
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                )
                .permitAll()
                
                .anyRequest()
                .authenticated()
            )

            // Deshabilitamos mecanismos que no usaremos
            .httpBasic(basic -> basic.disable())
           .formLogin(form -> form.disable())

         .addFilterBefore(
      jwtAuthenticationFilter,
       UsernamePasswordAuthenticationFilter.class
     );

        return http.build();
    }
}
