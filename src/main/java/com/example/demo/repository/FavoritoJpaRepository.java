package com.example.demo.repository;

import com.example.demo.entity.FavoritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoritoJpaRepository extends JpaRepository<FavoritoEntity, Long> {

    // Consulta derivada: Spring la arma sola a partir del nombre (lista.id)
    List<FavoritoEntity> findByListaId(Long listaId);
}