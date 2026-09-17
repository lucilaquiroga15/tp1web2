package com.example.demo.service;

import com.example.demo.client.dummyjson.DummyJsonClient;
import com.example.demo.client.dummyjson.DummyJsonProducto;
import com.example.demo.dto.producto.ProductoDTO;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final DummyJsonClient dummyJsonClient;

    public ProductoServiceImpl(DummyJsonClient dummyJsonClient) {
        this.dummyJsonClient = dummyJsonClient;
    }

   @Override
public List<ProductoDTO> listarTodos() {
    return dummyJsonClient.listarProductos()
            .products()
            .stream()
            .map(this::aProductoDTO)
            .toList();
}

    @Override
    public ProductoDTO obtenerPorId(Long id) {
        return aProductoDTO(dummyJsonClient.obtenerPorId(id));
    }

    private ProductoDTO aProductoDTO(DummyJsonProducto e) {
        return new ProductoDTO            (
                e.id(), e.title(), e.description(), e.category(), e.brand(),
                e.price(), e.discountPercentage(), e.stock(), e.rating(), e.thumbnail()
        );
    }
}