package com.example.demo.repository;

import com.example.demo.domain.Lista;

import java.util.List;
import java.util.Optional;

public interface ListaRepository {
    List<Lista> buscarTodos();
    Optional<Lista> buscarPorId(Long id);
    Lista save(Lista lista);
    void deleteById(Long id);
}