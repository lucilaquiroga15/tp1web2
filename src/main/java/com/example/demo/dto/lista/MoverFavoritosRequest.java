package com.example.demo.dto.lista;

import jakarta.validation.constraints.NotNull;

public record MoverFavoritosRequest(

    @NotNull(message = "El id de la lista destino no puede ser nulo")
    Long listaDestinoId

) {
}