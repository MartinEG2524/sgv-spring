package com.garritas.sgv.service;

import com.garritas.sgv.model.DetalleHistorialInventario;

import java.util.List;
import java.util.Optional;

public interface DetalleHistorialInventarioService {
    List<DetalleHistorialInventario> listar();
    Optional<DetalleHistorialInventario> buscarPorId(Long id);
    DetalleHistorialInventario guardar(DetalleHistorialInventario detalle);
    void eliminar(Long id);
}
