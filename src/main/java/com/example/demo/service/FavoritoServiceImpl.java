package com.example.demo.service;

import com.example.demo.domain.Favorito;
import com.example.demo.dto.favorito.FavoritoRequest;
import com.example.demo.dto.favorito.FavoritoResponse;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.repository.FavoritoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoritoServiceImpl implements FavoritoService {

    private final FavoritoRepository repository;

    public FavoritoServiceImpl(FavoritoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<FavoritoResponse> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::aResponse)
                .toList();
    }

    @Override
    public FavoritoResponse obtenerPorId(Long id) {
        return aResponse(buscarOFallar(id));
    }

    @Override
    public FavoritoResponse crear(FavoritoRequest request) {
        Favorito nuevo = new Favorito(null, request.productoId(), request.nota(), LocalDateTime.now());
        return aResponse(repository.save(nuevo));
    }

    @Override
    public FavoritoResponse actualizar(Long id, FavoritoRequest request) {
        Favorito existente = buscarOFallar(id);
        Favorito actualizado = new Favorito(
                existente.id(),
                request.productoId(),
                request.nota(),
                existente.fechaAgregado() // no se pisa la fecha original
        );
        return aResponse(repository.save(actualizado));
    }

    @Override
    public void eliminar(Long id) {
        buscarOFallar(id); // si no existe, tira 404 antes de intentar borrar
        repository.deleteById(id);
    }

    private Favorito buscarOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el favorito con id " + id));
    }

    private FavoritoResponse aResponse(Favorito f) {
        return new FavoritoResponse(f.id(), f.productoId(), f.nota(), f.fechaAgregado());
    }
}