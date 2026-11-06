
package com.example.demo.repository;

import com.example.demo.domain.Lista;
import com.example.demo.entity.ListaEntity;
import com.example.demo.entity.UsuarioEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ListaRepositoryAdapter implements ListaRepository {

    private final ListaJpaRepository jpaRepository;
    private final EntityManager entityManager;

    public ListaRepositoryAdapter(
            ListaJpaRepository jpaRepository,
            EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public List<Lista> buscarTodos() {
        return jpaRepository.findAll().stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    public Optional<Lista> buscarPorId(Long id) {
        return jpaRepository.findById(id)
                .map(this::aDominio);
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
        Long usuarioId = entity.getUsuario() == null
                ? null
                : entity.getUsuario().getId();

        return new Lista(
                entity.getId(),
                entity.getNombre(),
                usuarioId
        );
    }

    private ListaEntity aEntidad(Lista lista) {
        ListaEntity entity = new ListaEntity();

        entity.setId(lista.id());
        entity.setNombre(lista.nombre());

        if (lista.usuarioId() != null) {
            UsuarioEntity usuario = entityManager.getReference(
                    UsuarioEntity.class,
                    lista.usuarioId()
            );

            entity.setUsuario(usuario);
        }

        return entity;
    }
}
