package com.example.demo.dto.producto.Favorito;

import java.time.LocalDateTime;

public record FavoritoResponse(
        Long id,
        Long productoId,
        String nota,
        LocalDateTime fechaAgregado
) {
}