package com.garritas.sgv.service;

import com.garritas.sgv.model.Inventario;

import java.util.List;
import java.util.Optional;

public interface InventarioService {
    List<Inventario> listar();
    Optional<Inventario> buscarPorId(Long id);
    Inventario guardar(Inventario inventario);
    void eliminar(Long id);
}
