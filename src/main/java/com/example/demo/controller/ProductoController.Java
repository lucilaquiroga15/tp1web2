package com.example.demo.controller;

import com.example.demo.dto.producto.ProductoDTO;
import com.example.demo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController 
@RequestMapping("/api/productos")
@Tag(name="productos",description = "Catalogo de solo lectura (consumido de DummyJSON)")
public class ProductoController {

    private final ProductoService ProductoService; 

    public ProductoController (ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation (summary = "Lista todos los productos del catalogo")
    @GetMapping 
    public List <ProductoDTO> listar () {
        return productoService.listarTodos();
    }

    @Operation(summary = "Obtiene un producto por id")
    @GetMapping("/{id}")
    public ProductoDTO obtenerUno (@PathVariable Long id) {
        return productoService.ObtenerPorId(id);
    }
}