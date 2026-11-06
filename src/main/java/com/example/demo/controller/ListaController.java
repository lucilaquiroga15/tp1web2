package com.example.demo.controller;

import com.example.demo.config.OpenApiConfig;
import com.example.demo.dto.favorito.FavoritoResponse;
import com.example.demo.dto.lista.ListaRequest;
import com.example.demo.dto.lista.ListaResponse;
import com.example.demo.dto.lista.MoverFavoritosRequest;
import com.example.demo.service.FavoritoService;
import com.example.demo.service.ListaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/listas")
@Tag(name = "Listas",
        description = "Listas para organizar los favoritos. REQUIERE AUTENTICACIÓN (JWT). "
                + "Cada usuario solo accede a sus listas; un ADMIN accede a las de cualquiera.")
@SecurityRequirement(name = OpenApiConfig.ESQUEMA_JWT)
public class ListaController {

    private final ListaService listaService;
    private final FavoritoService favoritoService;

    public ListaController(ListaService listaService, FavoritoService favoritoService) {
        this.listaService = listaService;
        this.favoritoService = favoritoService;
    }

    @Operation(summary = "Crea una lista",
            description = "Devuelve 201 con la lista creada y su ubicación. El dueño es el usuario autenticado. "
                    + "401 sin token.")
    @PostMapping
    public ResponseEntity<ListaResponse> crear(@Valid @RequestBody ListaRequest request) {
        ListaResponse creada = listaService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.id())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @Operation(summary = "Lista las listas del usuario autenticado",
            description = "Devuelve solo las listas propias (todas, si el usuario es ADMIN). 401 sin token.")
    @GetMapping
    public List<ListaResponse> listar() {
        return listaService.buscarTodos();
    }

    @Operation(summary = "Obtiene una lista por id",
            description = "401 sin token, 403 si la lista no es del usuario (y no es ADMIN), 404 si no existe.")
    @GetMapping("/{id}")
    public ListaResponse buscar(@PathVariable Long id) {
        return listaService.buscarPorId(id);
    }

    @Operation(summary = "Lista los favoritos de una lista",
            description = "401 sin token, 403 si la lista no es del usuario (y no es ADMIN), 404 si no existe.")
    @GetMapping("/{id}/favoritos")
    public List<FavoritoResponse> favoritosDeLaLista(@PathVariable Long id) {
        return favoritoService.buscarPorListaId(id);
    }

    @Operation(summary = "Elimina una lista vacía",
            description = "401 sin token, 403 si la lista no es del usuario (y no es ADMIN), "
                    + "404 si no existe, 409 si todavía tiene favoritos.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        listaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mueve todos los favoritos de una lista a otra y elimina la lista origen",
            description = "Operación atómica (transaccional). Devuelve la lista destino. "
                    + "401 sin token, 403 si alguna de las dos listas no es del usuario (y no es ADMIN), "
                    + "404 si alguna no existe, 409 si origen y destino son la misma.")
    @PostMapping("/{origenId}/mover-favoritos")
    public ListaResponse moverFavoritos(@PathVariable Long origenId,
                                        @Valid @RequestBody MoverFavoritosRequest request) {
        return listaService.moverFavoritos(origenId, request.listaDestinoId());
    }
}