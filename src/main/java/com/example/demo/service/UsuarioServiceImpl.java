
package com.example.demo.service;

import com.example.demo.domain.Rol;
import com.example.demo.domain.Usuario;
import com.example.demo.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.example.demo.dto.Login.LoginResponse;

@Service
public class UsuarioServiceImpl {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioServiceImpl(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService) {

    this.usuarioRepository = usuarioRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
}

    public Usuario registrar(String email, String password) {

        String emailNormalizado = email.trim().toLowerCase();

        if (usuarioRepository.existePorEmail(emailNormalizado)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El email ya está registrado"
            );
        }

        String passwordHash = passwordEncoder.encode(password);

        Usuario nuevoUsuario = new Usuario(
                null,
                emailNormalizado,
                passwordHash,
                Rol.USER
        );

        return usuarioRepository.save(nuevoUsuario);
    }
    
public LoginResponse login(String email, String password) {

    String emailNormalizado = email.trim().toLowerCase();

    Usuario usuario = usuarioRepository
            .buscarPorEmail(emailNormalizado)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales incorrectas"
            ));

    if (!passwordEncoder.matches(
            password,
            usuario.passwordHash())) {

        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Credenciales incorrectas"
        );
    }

    String token = jwtService.generarToken(
            usuario.id(),
            usuario.email(),
            usuario.rol().name()
    );

    return new LoginResponse(token, "Bearer");
}

}
