package com.example.demo.controller;

import com.example.demo.dto.producto.ProductoDTO;
import com.example.demo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService service;

    public ProductoController(ProductoService service){
        this.service = service;
    }

    @GetMapping
    public List<ProductoDTO> listar() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ProductoDTO buscar(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }
    //Recibe las peticiones web y expone el catalogo al usuario.
}