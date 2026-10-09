package com.example.demo.service;

import com.example.demo.domain.Favorito;
import com.example.demo.domain.Lista;
import com.example.demo.dto.lista.ListaRequest;
import com.example.demo.dto.lista.ListaResponse;
import com.example.demo.exception.ConflictoException;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListaService {

    private final ListaRepository listaRepository;
    private final FavoritoRepository favoritoRepository;

    public ListaService(ListaRepository listaRepository, FavoritoRepository favoritoRepository) {
        this.listaRepository = listaRepository;
        this.favoritoRepository = favoritoRepository;
    }

    private ListaResponse mapearAResponse(Lista lista) {
        return new ListaResponse(lista.id(), lista.nombre());
    }

    public List<ListaResponse> buscarTodos() {
        return listaRepository.buscarTodos().stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public ListaResponse buscarPorId(Long id) {
        return listaRepository.buscarPorId(id)
                .map(this::mapearAResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la lista con id " + id));
    }

    public ListaResponse crear(ListaRequest request) {
        Lista nueva = new Lista(null, request.nombre());
        return mapearAResponse(listaRepository.save(nueva));
    }

    public void eliminar(Long id) {
        buscarPorId(id); // lanza 404 si no existe
        if (!favoritoRepository.buscarPorListaId(id).isEmpty()) {
            throw new ConflictoException(
                    "No se puede eliminar la lista " + id + " porque todavía tiene favoritos");
        }
        listaRepository.deleteById(id);
    }

    /**
     * Reasigna todos los favoritos de la lista origen a la lista destino y elimina
     * la lista origen. Es UNA sola unidad de trabajo: si cualquier escritura falla,
     * se deshace todo (rollback) y la base queda como estaba.
     */
    @Transactional
    public ListaResponse moverFavoritos(Long origenId, Long destinoId) {
        buscarPorId(origenId);                           // 404 si no existe el origen
        ListaResponse destino = buscarPorId(destinoId);  // 404 si no existe el destino

        if (origenId.equals(destinoId)) {
            throw new ConflictoException("La lista origen y la lista destino no pueden ser la misma");
        }

        // Escrituras 1..n: reasignar cada favorito a la lista destino
        for (Favorito favorito : favoritoRepository.buscarPorListaId(origenId)) {
            favoritoRepository.save(new Favorito(
                    favorito.id(),
                    favorito.productoId(),
                    favorito.nota(),
                    favorito.fechaAgregado(),
                    destinoId
            ));
        }

        // Última escritura: eliminar la lista origen, que ya quedó vacía
        listaRepository.deleteById(origenId);

        return destino;
    }
}