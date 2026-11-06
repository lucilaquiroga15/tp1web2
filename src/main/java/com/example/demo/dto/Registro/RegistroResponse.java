package com.example.demo.dto.Registro;

import com.example.demo.domain.Rol;

public record RegistroResponse(
    Long id,
    String email, 
    Rol rol

) {

}

