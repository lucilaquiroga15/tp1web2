package com.example.demo.client.dummyjson;

import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.exception.ServicioExternoException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class DummyJsonClient {

    private final RestClient restClient;

    public DummyJsonClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public DummyJsonProductosResponse listarProductos() {
        try {
            return restClient.get()
                    .uri("/products")
                    .retrieve()
                    .body(DummyJsonProductosResponse.class);
        } catch (ResourceAccessException e) {
            throw new ServicioExternoException("No se pudo contactar a DummyJSON", e);
        }
    }

    public DummyJsonProducto obtenerPorId(Long id) {
        try {
            return restClient.get()
                    .uri("/products/{id}", id)
                    .retrieve()
                    .body(DummyJsonProducto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("No existe el producto con id " + id);
        } catch (ResourceAccessException e) {
            throw new ServicioExternoException("No se pudo contactar a DummyJSON", e);
        }
    }
}