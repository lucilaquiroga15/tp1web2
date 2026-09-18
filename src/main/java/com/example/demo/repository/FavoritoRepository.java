package com.example.demo.repository;

import com.example.demo.domain.Favorito;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepository {
    List<Favorito> buscarTodos();
    Optional<Favorito> buscarPorId(Long id);
    Favorito save(Favorito favorito);
    void deleteById(Long id);
}