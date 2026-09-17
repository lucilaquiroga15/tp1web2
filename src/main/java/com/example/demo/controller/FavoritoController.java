package com.example.demo.controller;

import com.example.demo.dto.favorito.FavoritoRequest;
import com.example.demo.dto.favorito.FavoritoResponse;
import com.example.demo.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@Tag(name = "favoritos", description = "CRUD en memoria")
public class FavoritoController {

    private final FavoritoService service;

    public FavoritoController(FavoritoService service) {
        this.service = service;
    }

    @Operation(summary = "Crear un favorito")
    @PostMapping
    public ResponseEntity<FavoritoResponse> crear(@Valid @RequestBody FavoritoRequest request) {
        FavoritoResponse creado = service.crear(request);
        return ResponseEntity.created(URI.create("/api/favoritos/" + creado.id())).body(creado);
    }

    @Operation(summary = "Listar todos los favoritos")
    @GetMapping
    public List<FavoritoResponse> listar() {
        return service.listarTodos();
    }

    @Operation(summary = "Obtener un favorito por id")
    @GetMapping("/{id}")
    public FavoritoResponse obtenerUno(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @Operation(summary = "Actualizar un favorito")
    @PutMapping("/{id}")
    public FavoritoResponse actualizar(@PathVariable Long id, @Valid @RequestBody FavoritoRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Eliminar un favorito")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}