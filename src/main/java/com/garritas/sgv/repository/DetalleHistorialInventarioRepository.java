package com.garritas.sgv.repository;

import com.garritas.sgv.model.DetalleHistorialInventario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleHistorialInventarioRepository extends JpaRepository<DetalleHistorialInventario, Long> {
    boolean existsByHistorial_IdHistorial(Long idHistorial);
    boolean existsByProducto_IdProducto(Long idProducto);
}
