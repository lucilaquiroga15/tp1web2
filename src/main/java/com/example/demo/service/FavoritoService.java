
package com.example.demo.service;

import com.example.demo.domain.Favorito;
import com.example.demo.domain.Lista;
import com.example.demo.domain.Rol;
import com.example.demo.domain.Usuario;
import com.example.demo.dto.favorito.FavoritoRequest;
import com.example.demo.dto.favorito.FavoritoResponse;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FavoritoService {

    private final FavoritoRepository repository;
    private final ListaRepository listaRepository;
    private final UsuarioRepository usuarioRepository;

    public FavoritoService(
            FavoritoRepository repository,
            ListaRepository listaRepository,
            UsuarioRepository usuarioRepository) {

        this.repository = repository;
        this.listaRepository = listaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Convertir favorito de dominio a DTO
    private FavoritoResponse mapearAResponse(Favorito favorito) {
        return new FavoritoResponse(
                favorito.id(),
                favorito.productoId(),
                favorito.nota(),
                favorito.fechaAgregado(),
                favorito.listaId()
        );
    }

    // Obtener el usuario que inició sesión con JWT
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

        return usuarioRepository
                .buscarPorEmail(authentication.getName())
                .orElseThrow(() ->
                        new AuthenticationCredentialsNotFoundException(
                                "Usuario autenticado no encontrado"
                        )
                );
    }

    // Verificar si el usuario es administrador
    private boolean esAdmin(Usuario usuario) {
        return usuario.rol() == Rol.ADMIN;
    }

    // Buscar lista y verificar permisos
    private void verificarPermisoLista(Long listaId, Usuario usuario) {

        Lista lista = listaRepository.buscarPorId(listaId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la lista con id " + listaId
                        )
                );

        if (!esAdmin(usuario)
                && !usuario.id().equals(lista.usuarioId())) {

            throw new AccessDeniedException(
                    "No tenés permiso para acceder a esta lista"
            );
        }
    }

    // Listar únicamente favoritos permitidos
    public List<FavoritoResponse> buscarTodos() {

        Usuario usuario = obtenerUsuarioAutenticado();

        return repository.buscarTodos().stream()
                .filter(favorito -> {
                    Optional<Lista> lista =
                            listaRepository.buscarPorId(favorito.listaId());

                    return lista.isPresent()
                            && (esAdmin(usuario)
                            || usuario.id().equals(lista.get().usuarioId()));
                })
                .map(this::mapearAResponse)
                .toList();
    }

    // Buscar un favorito por ID
    public Optional<FavoritoResponse> buscarPorId(Long id) {

        Usuario usuario = obtenerUsuarioAutenticado();

        return repository.buscarPorId(id)
                .map(favorito -> {
                    verificarPermisoLista(favorito.listaId(), usuario);
                    return mapearAResponse(favorito);
                });
    }

    // Buscar favoritos de una lista
    public List<FavoritoResponse> buscarPorListaId(Long listaId) {

        Usuario usuario = obtenerUsuarioAutenticado();

        verificarPermisoLista(listaId, usuario);

        return repository.buscarPorListaId(listaId).stream()
                .map(this::mapearAResponse)
                .toList();
    }

    // Crear favorito en una lista propia
    @Transactional
    public FavoritoResponse crear(FavoritoRequest request) {

        Usuario usuario = obtenerUsuarioAutenticado();

        verificarPermisoLista(request.listaId(), usuario);

        Favorito nuevoFavorito = new Favorito(
                null,
                request.productoId(),
                request.nota(),
                LocalDateTime.now(),
                request.listaId()
        );

        return mapearAResponse(repository.save(nuevoFavorito));
    }

    // Actualizar favorito
    @Transactional
    public Optional<FavoritoResponse> actualizar(
            Long id,
            FavoritoRequest request) {

        Usuario usuario = obtenerUsuarioAutenticado();

        return repository.buscarPorId(id).map(existente -> {

            // Validar permiso sobre la lista original
            verificarPermisoLista(existente.listaId(), usuario);

            // Validar permiso sobre la lista de destino
            verificarPermisoLista(request.listaId(), usuario);

            Favorito actualizado = new Favorito(
                    existente.id(),
                    request.productoId(),
                    request.nota(),
                    existente.fechaAgregado(),
                    request.listaId()
            );

            return mapearAResponse(repository.save(actualizado));
        });
    }

    // Eliminar favorito
    @Transactional
    public void deleteById(Long id) {

        Usuario usuario = obtenerUsuarioAutenticado();

        Favorito favorito = repository.buscarPorId(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe el favorito con id " + id
                        )
                );

        verificarPermisoLista(favorito.listaId(), usuario);

        repository.deleteById(id);
    }
}
