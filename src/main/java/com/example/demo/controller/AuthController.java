package com.example.demo.controller;

import com.example.demo.domain.Usuario;
import com.example.demo.dto.Registro.RegistroRequest;
import com.example.demo.dto.Registro.RegistroResponse;
import com.example.demo.service.UsuarioServiceImpl;
import com.example.demo.dto.Login.LoginRequest;
import com.example.demo.dto.Login.LoginResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioServiceImpl usuarioService;

    public AuthController(UsuarioServiceImpl usuarioService) {
        this.usuarioService = usuarioService;
    }

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