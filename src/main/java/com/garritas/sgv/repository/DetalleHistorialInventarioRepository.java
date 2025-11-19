package com.garritas.sgv.repository;

import com.garritas.sgv.model.DetalleHistorialInventario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleHistorialInventarioRepository extends JpaRepository<DetalleHistorialInventario, Long> {
    Optional<DetalleHistorialInventario> findByHistorial_IdHistorial(Long idHistorial);
    Optional<DetalleHistorialInventario> findByProducto_IdProducto(Long idProducto);
}
