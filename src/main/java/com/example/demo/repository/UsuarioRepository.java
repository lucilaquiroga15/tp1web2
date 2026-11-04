
package com.example.demo.repository;

import com.example.demo.domain.Usuario;
import java.util.Optional;

public interface UsuarioRepository {

    Usuario save(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

    boolean existePorEmail(String email);
}
