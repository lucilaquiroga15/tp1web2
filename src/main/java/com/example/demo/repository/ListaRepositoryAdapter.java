package com.example.demo.repository;

import com.example.demo.domain.Lista;
import com.example.demo.entity.ListaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ListaRepositoryAdapter implements ListaRepository {

    private final ListaJpaRepository jpaRepository;

    public ListaRepositoryAdapter(ListaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Lista> buscarTodos() {
        return jpaRepository.findAll().stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    public Optional<Lista> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::aDominio);
    }

    @Override
    public Lista save(Lista lista) {
        return aDominio(jpaRepository.save(aEntidad(lista)));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Lista aDominio(ListaEntity entity) {
        return new Lista(entity.getId(), entity.getNombre());
    }

    private ListaEntity aEntidad(Lista lista) {
        ListaEntity entity = new ListaEntity();
        entity.setId(lista.id());
        entity.setNombre(lista.nombre());
        return entity;
    }
}