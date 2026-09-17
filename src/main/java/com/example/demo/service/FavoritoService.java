package com.example.demo.service;

import com.example.demo.dto.favorito.FavoritoRequest;
import com.example.demo.dto.favorito.FavoritoResponse;

import java.util.List;

public interface FavoritoService {
        List <FavoritoResponse> listarTodos ();
        FavoritoResponse obtenerPorId(Long id);
        FavoritoResponse crear(FavoritoRequest request);
        FavoritoResponse actualizar (Long id, FavoritoRequest request);
        void eliminar (Long id);
}