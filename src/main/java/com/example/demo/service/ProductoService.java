package com.example.demo.service;
import com.example.demo.dto.producto.ProductoDTO;
import java.util.List;

public interface ProductoService {
    List <ProductoDTO> listarTodos();
    ProductoDTO obtenerPorId (Long id);
}