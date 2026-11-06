package com.example.demo.controller;

import com.example.demo.domain.Usuario;
import com.example.demo.dto.Login.LoginRequest;
import com.example.demo.dto.Login.LoginResponse;
import com.example.demo.dto.Registro.RegistroRequest;
import com.example.demo.dto.Registro.RegistroResponse;
import com.example.demo.service.UsuarioServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro y login. Endpoints públicos, no requieren token.")
public class AuthController {

    private final UsuarioServiceImpl usuarioService;

    public AuthController(UsuarioServiceImpl usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Registra un usuario",
            description = "Crea un usuario con rol USER y la contraseña hasheada. "
                    + "201 si se creó, 400 si los datos son inválidos, 409 si el email ya existe.")
    @PostMapping("/registro")
    public ResponseEntity<RegistroResponse> registrar(
            @Valid @RequestBody RegistroRequest request) {

        Usuario usuario = usuarioService.registrar(
                request.email(),
                request.password()
        );

        RegistroResponse response = new RegistroResponse(
                usuario.id(),
                usuario.email(),
                usuario.rol()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Inicia sesión y devuelve un JWT",
            description = "Devuelve 200 con un token firmado (lleva el id y el rol del usuario). "
                    + "Usalo como Bearer en el resto de los endpoints. 401 si las credenciales son incorrectas.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = usuarioService.login(
                request.email(),
                request.password()
        );

        return ResponseEntity.ok(response);
    }
}