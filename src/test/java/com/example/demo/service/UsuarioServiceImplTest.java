package com.example.demo.service;

import com.example.demo.domain.Rol;
import com.example.demo.domain.Usuario;
import com.example.demo.exception.ConflictoException;
import com.example.demo.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void registrar_passwordValida_guardaHashYNoTextoPlano() {
        String passwordPlano = "clave12345";

        when(usuarioRepository.existePorEmail("lucila@test.com")).thenReturn(false);
        when(passwordEncoder.encode(passwordPlano)).thenReturn("HASH_SIMULADO");
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        usuarioService.registrar("lucila@test.com", passwordPlano);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario guardado = captor.getValue();

        assertThat(guardado.passwordHash()).isEqualTo("HASH_SIMULADO");
        assertThat(guardado.passwordHash()).isNotEqualTo(passwordPlano);
        assertThat(guardado.rol()).isEqualTo(Rol.ADMIN);    }

    @Test
    void registrar_emailExistente_lanzaConflicto() {
        when(usuarioRepository.existePorEmail("lucila@test.com")).thenReturn(true);

        assertThatThrownBy(() ->
                usuarioService.registrar("lucila@test.com", "clave12345"))
                .isInstanceOf(ConflictoException.class);

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}