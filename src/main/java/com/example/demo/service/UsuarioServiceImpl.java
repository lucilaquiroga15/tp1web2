
package com.example.demo.service;

import com.example.demo.domain.Rol;
import com.example.demo.domain.Usuario;
import com.example.demo.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class UsuarioServiceImpl {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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
}
