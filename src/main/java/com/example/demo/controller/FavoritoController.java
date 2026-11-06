package com.example.demo.controller;

import com.example.demo.config.OpenApiConfig;
import com.example.demo.dto.favorito.FavoritoRequest;
import com.example.demo.dto.favorito.FavoritoResponse;
import com.example.demo.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@Tag(name = "Favoritos",
        description = "CRUD de productos favoritos, persistidos en PostgreSQL. REQUIERE AUTENTICACIÓN (JWT). "
                + "Cada favorito cuelga de una lista: solo se opera sobre favoritos de listas propias "
                + "(un ADMIN puede operar sobre las de cualquiera).")
@SecurityRequirement(name = OpenApiConfig.ESQUEMA_JWT)
public class FavoritoController {

    private final FavoritoService service;

    public FavoritoController(FavoritoService service) {
        this.service = service;
    }

    @Operation(summary = "Lista los favoritos del usuario autenticado",
            description = "Solo los favoritos de sus listas (todos, si es ADMIN). 401 sin token.")
    @GetMapping
    public List<FavoritoResponse> listar() {
        return service.buscarTodos();
    }

    @Operation(summary = "Obtiene un favorito por id",
            description = "401 sin token, 403 si la lista del favorito no es del usuario, 404 si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<FavoritoResponse> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crea un nuevo favorito",
            description = "401 sin token, 403 si la lista indicada no es del usuario, 404 si la lista no existe.")
    @PostMapping
    public ResponseEntity<FavoritoResponse> crear(@Valid @RequestBody FavoritoRequest request) {
        FavoritoResponse creado = service.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualiza un favorito existente",
            description = "401 sin token, 403 si la lista de origen o de destino no es del usuario, 404 si no existe.")
    @PutMapping("/{id}")
    public ResponseEntity<FavoritoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody FavoritoRequest request) {
        return service.actualizar(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Elimina un favorito",
            description = "401 sin token, 403 si la lista del favorito no es del usuario, 404 si no existe.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}