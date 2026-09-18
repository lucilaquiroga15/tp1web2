package com.example.demo.controller;

import jakarta.validation.Valid;
import com.example.demo.dto.favorito.FavoritoRequest;
import com.example.demo.dto.favorito.FavoritoResponse;
import com.example.demo.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@Tag(name = "Favoritos", description = "CRUD en memoria de productos favoritos")
public class FavoritoController {

    private final FavoritoService service;

    public FavoritoController(FavoritoService service) {
        this.service = service;
    }

    @Operation(summary = "Lista todos los favoritos")
    @GetMapping
    public List<FavoritoResponse> listar() {
        return service.buscarTodos();
    }

    @Operation(summary = "Obtiene un favorito por id")
    @GetMapping("/{id}")
    public ResponseEntity<FavoritoResponse> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crea un nuevo favorito")
    @PostMapping
    public ResponseEntity<FavoritoResponse> crear(@Valid @RequestBody FavoritoRequest request) {
        FavoritoResponse creado = service.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualiza un favorito existente")
    @PutMapping("/{id}")
    public ResponseEntity<FavoritoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody FavoritoRequest request) {
        return service.actualizar(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Elimina un favorito")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}