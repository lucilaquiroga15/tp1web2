package com.example.demo.service;

import com.example.demo.domain.Favorito;
import com.example.demo.dto.favorito.FavoritoRequest;
import com.example.demo.dto.favorito.FavoritoResponse;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FavoritoService {

    private final FavoritoRepository repository;
    private final ListaRepository listaRepository;

    public FavoritoService(FavoritoRepository repository, ListaRepository listaRepository) {
        this.repository = repository;
        this.listaRepository = listaRepository;
    }

    // MAPEO DE DOMINIO A DTO
    private FavoritoResponse mapearAResponse(Favorito favorito) {
        return new FavoritoResponse(
                favorito.id(),
                favorito.productoId(),
                favorito.nota(),
                favorito.fechaAgregado(),
                favorito.listaId()
        );
    }

    // Si la lista no existe, respondemos 404 en vez de dejar que falle la clave foránea
    private void validarQueExisteLista(Long listaId) {
        if (listaRepository.buscarPorId(listaId).isEmpty()) {
            throw new RecursoNoEncontradoException("No existe la lista con id " + listaId);
        }
    }

    public List<FavoritoResponse> buscarTodos() {
        return repository.buscarTodos().stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public Optional<FavoritoResponse> buscarPorId(Long id) {
        return repository.buscarPorId(id)
                .map(this::mapearAResponse);
    }

    public List<FavoritoResponse> buscarPorListaId(Long listaId) {
        validarQueExisteLista(listaId);
        return repository.buscarPorListaId(listaId).stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public FavoritoResponse crear(FavoritoRequest request) {
        validarQueExisteLista(request.listaId());
        Favorito nuevoFavorito = new Favorito(
                null, // El repositorio se encarga de generar el ID
                request.productoId(),
                request.nota(),
                LocalDateTime.now(),
                request.listaId()
        );
        return mapearAResponse(repository.save(nuevoFavorito));
    }

    // ACTUALIZAR UN FAVORITO QUE YA EXISTE
    public Optional<FavoritoResponse> actualizar(Long id, FavoritoRequest request) {
        return repository.buscarPorId(id).map(existente -> {
            validarQueExisteLista(request.listaId());
            Favorito actualizado = new Favorito(
                    existente.id(),            // ID original
                    request.productoId(),
                    request.nota(),
                    existente.fechaAgregado(), // Se mantiene la fecha original
                    request.listaId()
            );
            return mapearAResponse(repository.save(actualizado));
        });
    }

    // ELIMINAR UN FAVORITO
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}