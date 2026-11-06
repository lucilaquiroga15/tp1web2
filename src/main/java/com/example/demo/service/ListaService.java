
package com.example.demo.service;

import com.example.demo.domain.Favorito;
import com.example.demo.domain.Lista;
import com.example.demo.domain.Rol;
import com.example.demo.domain.Usuario;
import com.example.demo.dto.lista.ListaRequest;
import com.example.demo.dto.lista.ListaResponse;
import com.example.demo.exception.ConflictoException;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import com.example.demo.repository.UsuarioRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListaService {

    private final ListaRepository listaRepository;
    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;

    public ListaService(
            ListaRepository listaRepository,
            FavoritoRepository favoritoRepository,
            UsuarioRepository usuarioRepository) {

        this.listaRepository = listaRepository;
        this.favoritoRepository = favoritoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private ListaResponse mapearAResponse(Lista lista) {
        return new ListaResponse(lista.id(), lista.nombre());
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {
            throw new AuthenticationCredentialsNotFoundException(
                    "Usuario no autenticado"
            );
        }

        return usuarioRepository.buscarPorEmail(authentication.getName())
                .orElseThrow(() ->
                        new AuthenticationCredentialsNotFoundException(
                                "Usuario autenticado no encontrado"
                        )
                );
    }

    private boolean esAdmin(Usuario usuario) {
        return usuario.rol() == Rol.ADMIN;
    }

    private void verificarPermiso(Lista lista, Usuario usuario) {
        if (!esAdmin(usuario)
                && !usuario.id().equals(lista.usuarioId())) {
            throw new AccessDeniedException(
                    "No tenés permiso para acceder a esta lista"
            );
        }
    }

    private Lista obtenerLista(Long id) {
        return listaRepository.buscarPorId(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la lista con id " + id
                        )
                );
    }

    public List<ListaResponse> buscarTodos() {
        Usuario usuario = obtenerUsuarioAutenticado();

        return listaRepository.buscarTodos().stream()
                .filter(lista ->
                        esAdmin(usuario)
                        || usuario.id().equals(lista.usuarioId())
                )
                .map(this::mapearAResponse)
                .toList();
    }

    public ListaResponse buscarPorId(Long id) {
        Usuario usuario = obtenerUsuarioAutenticado();
        Lista lista = obtenerLista(id);

        verificarPermiso(lista, usuario);

        return mapearAResponse(lista);
    }

    public ListaResponse crear(ListaRequest request) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Lista nueva = new Lista(
                null,
                request.nombre(),
                usuario.id()
        );

        return mapearAResponse(listaRepository.save(nueva));
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtenerUsuarioAutenticado();
        Lista lista = obtenerLista(id);

        verificarPermiso(lista, usuario);

        if (!favoritoRepository.buscarPorListaId(id).isEmpty()) {
            throw new ConflictoException(
                    "No se puede eliminar la lista " + id
                    + " porque todavía tiene favoritos"
            );
        }

        listaRepository.deleteById(id);
    }

    @Transactional
    public ListaResponse moverFavoritos(Long origenId, Long destinoId) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Lista origen = obtenerLista(origenId);
        Lista destino = obtenerLista(destinoId);

        verificarPermiso(origen, usuario);
        verificarPermiso(destino, usuario);

        if (origenId.equals(destinoId)) {
            throw new ConflictoException(
                    "La lista origen y la lista destino no pueden ser la misma"
            );
        }

        for (Favorito favorito :
                favoritoRepository.buscarPorListaId(origenId)) {

            favoritoRepository.save(new Favorito(
                    favorito.id(),
                    favorito.productoId(),
                    favorito.nota(),
                    favorito.fechaAgregado(),
                    destinoId
            ));
        }

        listaRepository.deleteById(origenId);

        return mapearAResponse(destino);
    }
}
