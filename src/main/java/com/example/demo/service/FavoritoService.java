package com.example.demo.service;

import com.example.demo.domain.Favorito;
import com.example.demo.dto.favorito.FavoritoRequest;
import com.example.demo.dto.favorito.FavoritoResponse;
import com.example.demo.repository.FavoritoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
/*
*
* Traduce manualmente los datos entre la base de datos y tus DTOs.
*
 */
@Service
public class FavoritoService {
    private final FavoritoRepository repository;

    public FavoritoService(FavoritoRepository repository) {
        this.repository = repository;
    }

    //MAPEO  DE ENTIDAD A DTO
    private FavoritoResponse mapearAResponse(Favorito favorito) {
        return new FavoritoResponse(
            favorito.id(),
            favorito.productoId(),
            favorito.nota(),
            favorito.fechaAgregado()

                );
    }
     //METODOS DE EL MAPEO
    public List<FavoritoResponse> buscarTodos() { //obtener todos los favoritos
        return repository.buscarTodos().stream()
            .map(favorito -> mapearAResponse(favorito))
            .toList();
    }
    
    public Optional<FavoritoResponse> buscarPorId(Long id) {
        return repository.buscarPorId(id)
        .map(favorito -> mapearAResponse(favorito));
    }

    public FavoritoResponse crear(FavoritoRequest request) {
        Favorito nuevoFavorito = new Favorito(
            null, // El repositorio se encarga de generar el ID
            request.productoId(),
            request.nota(),
            LocalDateTime.now()
        );
        return mapearAResponse(repository.save(nuevoFavorito));
        //Favorito guardado = repository.guardar(nuevoFavorito);
        //return mapearAResponse(guardado);
    } 
        //ACTUALIZAR UN FAVORITO QUE YA EXISTE
    public Optional<FavoritoResponse> actualizar(Long id, FavoritoRequest request) {
        return repository.buscarPorId(id).map(existente -> {
            Favorito actualizado = new Favorito(
                existente.id(), //ID original
                request.productoId(),
                request.nota(),
                existente.fechaAgregado() //Se mantiene la fecha original
            );
            return mapearAResponse(repository.save(actualizado));
        });
    }

    //ELIMINAR UN FAVORITO
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

}