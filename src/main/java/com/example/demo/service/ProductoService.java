package com.example.demo.service;
import com.example.demo.dto.producto.ProductoDTO;
import java.util.List;
import org.springframework.web.client.RestClient;
import java.math.BigDecimal;
import com.example.demo.client.dummyjson.DummyJsonProducto;
import com.example.demo.client.dummyjson.DummyJsonProductosResponse;
import org.springframework.stereotype.Service;
/*
*
* Consume la API externa DummyJSON y mapea la información obtenida.
*
*/
@Service
public class ProductoService {
    private final RestClient restClient;
    public ProductoService() {
        this.restClient = RestClient.builder().baseUrl("https://dummyjson.com").build();
    }
 // OBTENER TODS
    public List<ProductoDTO> obtenerTodos() {
        DummyJsonProductosResponse respuesta = restClient.get()
                .uri("/products")
                .retrieve()
                .body(DummyJsonProductosResponse.class);

        if (respuesta == null || respuesta.products() == null) {
            return List.of();
        }


        // Mapeamos cada producto externo a tu ProductoDTO
        return respuesta.products().stream()
                .map(this::mapearADto)
                .toList();
    }
 //OBTENER POR ID
    public ProductoDTO obtenerPorId(Long id) {
        DummyJsonProducto externo = restClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .body(DummyJsonProducto.class);
                
        return mapearADto(externo);
    }
     // MAPEAR A DTO
    private ProductoDTO mapearADto(DummyJsonProducto externo) {
        if (externo == null) return null;
        return new ProductoDTO(
            externo.id(),
            externo.title(),
            externo.description(),
            externo.category(),
            externo.brand(),
            externo.price(),
            externo.discountPercentage(),
            externo.stock(),
            externo.rating(),
            externo.thumbnail()
        );
    }
}