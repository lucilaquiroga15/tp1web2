package com.example.demo.service;

import com.example.demo.domain.Lista;
import com.example.demo.domain.Rol;
import com.example.demo.domain.Usuario;
import com.example.demo.dto.lista.ListaRequest;
import com.example.demo.dto.lista.ListaResponse;
import com.example.demo.repository.FavoritoRepository;
import com.example.demo.repository.ListaRepository;
import com.example.demo.repository.UsuarioRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ListaServiceTest {

    @Mock
    private ListaRepository listaRepository;

    @Mock
    private FavoritoRepository favoritoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ListaService listaService;

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    // Simula que el filtro JWT dejo autenticado a este usuario
    private void autenticarComo(Usuario usuario) {
        var autenticacion = new UsernamePasswordAuthenticationToken(
                usuario.email(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.rol().name()))
        );
        SecurityContextHolder.getContext().setAuthentication(autenticacion);
        when(usuarioRepository.buscarPorEmail(usuario.email()))
                .thenReturn(Optional.of(usuario));
    }

    @Test
    void crear_usuarioAutenticado_listaQuedaAsociadaAlUsuario() {
        Usuario lucila = new Usuario(7L, "lucila@test.com", "hash", Rol.USER);
        autenticarComo(lucila);
        when(listaRepository.save(any(Lista.class)))
                .thenAnswer(invocacion -> {
                    Lista l = invocacion.getArgument(0);
                    return new Lista(1L, l.nombre(), l.usuarioId());
                });

        ListaResponse respuesta = listaService.crear(new ListaRequest("Mis favoritos"));

        ArgumentCaptor<Lista> captor = ArgumentCaptor.forClass(Lista.class);
        verify(listaRepository).save(captor.capture());

        assertThat(captor.getValue().usuarioId()).isEqualTo(7L);
        assertThat(captor.getValue().nombre()).isEqualTo("Mis favoritos");
        assertThat(respuesta.nombre()).isEqualTo("Mis favoritos");
    }

    @Test
    void buscarPorId_listaAjena_lanzaAccesoDenegado() {
        Usuario lucila = new Usuario(7L, "lucila@test.com", "hash", Rol.USER);
        autenticarComo(lucila);
        when(listaRepository.buscarPorId(99L))
                .thenReturn(Optional.of(new Lista(99L, "Ajena", 8L)));

        assertThatThrownBy(() -> listaService.buscarPorId(99L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void buscarPorId_adminConListaAjena_devuelveLaLista() {
        Usuario admin = new Usuario(1L, "admin@test.com", "hash", Rol.ADMIN);
        autenticarComo(admin);
        when(listaRepository.buscarPorId(99L))
                .thenReturn(Optional.of(new Lista(99L, "Ajena", 8L)));

        ListaResponse respuesta = listaService.buscarPorId(99L);

        assertThat(respuesta.id()).isEqualTo(99L);
    }
}