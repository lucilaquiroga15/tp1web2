package com.example.demo.controller;

import jakarta.validation.Valid; 
import com.example.demo.dto.favorito.FavoritoRequest;
import com.example.demo.dto.favorito.FavoritoResponse;
import com.example.demo.service.FavoritoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/favoritos") //Define la ruta base para todos los metodos
public class FavoritoController {

    private final FavoritoService service;

    public FavoritoController(FavoritoService service) {
        this.service = service;
    }


    @GetMapping
    //Listar
    public List<FavoritoResponse> listar() {
        return service.buscarTodos(); //Spring responde 200 OK por defecto
    }



    //GET - exito: 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<FavoritoResponse> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                //devuelve 404
                .orElse(ResponseEntity.notFound().build());
    }



    //Crear: POST - 201 Created
    @PostMapping
    public ResponseEntity<FavoritoResponse> crear(
        @Valid @RequestBody FavoritoRequest request) {
        FavoritoResponse creado = service.crear(request);
        //ResponseEntity para forzar el codigo 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }



    //Actualizar: PUT - 200 OK
    @PutMapping("/{id}")
    public ResponseEntity<FavoritoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody FavoritoRequest request) {
        
        return service.actualizar(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }




    //Eliminar: DELETE | 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.deleteById(id);
        //204 No Content indicando que se borró y no hay cuerpo en la respuesta
        return ResponseEntity.noContent().build();
    }
}