package com.example.demo.service;

import java.util.List;

public interface ProductoService {
    List <ProductoDTO> listarTodos();
    ProductoDTO ObtenerPorId (Long id);
}