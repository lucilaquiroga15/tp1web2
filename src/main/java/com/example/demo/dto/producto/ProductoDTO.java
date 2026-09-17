package com.example.demo.dto.producto;

public record ProductoDTO(
    Long id, 
    String nombre, 
    String descripcion,
    String categoria,
    String marca, 
    double precio,
    double descuentoPorcentaje,
    int stock,
    double calificacion, 
    String imagenUrl
){
    
}