package com.example.demo.repository;

import com.example.demo.domain.Favorito;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryFavoritoRepository implements FavoritoRepository {

    private final ConcurrentHashMap<Long, Favorito> datos = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public List<Favorito> buscarTodos() {
        return List.copyOf(datos.values());
    }

    @Override
    public Optional<Favorito> buscarPorId(Long id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public Favorito save(Favorito favorito) {
        Long id = favorito.id() != null ? favorito.id() : nextId.getAndIncrement();
        Favorito favoritoConId = new Favorito(id, favorito.productoId(), favorito.nota(), favorito.fechaAgregado());
        datos.put(id, favoritoConId);
        return favoritoConId;
    }

    @Override
    public void deleteById(Long id) {
        datos.remove(id);
    }
}