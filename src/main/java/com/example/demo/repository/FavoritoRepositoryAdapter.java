package com.example.demo.repository;

import com.example.demo.domain.Favorito;
import com.example.demo.entity.FavoritoEntity;
import com.example.demo.entity.ListaEntity;
import com.example.demo.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FavoritoRepositoryAdapter implements FavoritoRepository {

    private final FavoritoJpaRepository jpaRepository;
    private final ListaJpaRepository listaJpaRepository;

    public FavoritoRepositoryAdapter(FavoritoJpaRepository jpaRepository,
                                     ListaJpaRepository listaJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.listaJpaRepository = listaJpaRepository;
    }

    @Override
    public List<Favorito> buscarTodos() {
        return jpaRepository.findAll().stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    public Optional<Favorito> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::aDominio);
    }

    @Override
    public List<Favorito> buscarPorListaId(Long listaId) {
        return jpaRepository.findByListaId(listaId).stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    public Favorito save(Favorito favorito) {
        return aDominio(jpaRepository.save(aEntidad(favorito)));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    // Entidad JPA -> record del dominio
    private Favorito aDominio(FavoritoEntity entity) {
        Long listaId = entity.getLista() != null ? entity.getLista().getId() : null;
        return new Favorito(
                entity.getId(),
                entity.getProductoId(),
                entity.getNota(),
                entity.getFechaAlta(),
                listaId
        );
    }

    // Record del dominio -> entidad JPA
    private FavoritoEntity aEntidad(Favorito favorito) {
        FavoritoEntity entity = new FavoritoEntity();
        entity.setId(favorito.id()); // null = INSERT, con id = UPDATE
        entity.setProductoId(favorito.productoId());
        entity.setNota(favorito.nota());
        entity.setFechaAlta(favorito.fechaAgregado());
        if (favorito.listaId() != null) {
            ListaEntity lista = listaJpaRepository.findById(favorito.listaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe la lista con id " + favorito.listaId()));
            entity.setLista(lista);
        }
        return entity;
    }
}