package com.example.demo.dto.producto.Favorito;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FavoritoRequest(

    @NotNull (message = "El id del producto no puede ser nulo")
    Long productoId,

    @NotBlank (message = "La nota no puede estar vacía")
    String nota

) {
}