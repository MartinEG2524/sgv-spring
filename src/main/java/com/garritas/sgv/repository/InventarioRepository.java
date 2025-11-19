package com.garritas.sgv.repository;

import com.garritas.sgv.model.Inventario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByNombre(String nombre);
}
