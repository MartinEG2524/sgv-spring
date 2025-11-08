package com.garritas.sgv.service;

import com.garritas.sgv.model.HistorialClinico;

import java.util.List;
import java.util.Optional;

public interface HistorialClinicoService {
    List<HistorialClinico> listar();
    Optional<HistorialClinico> buscarPorId(Long id);
    HistorialClinico guardar(HistorialClinico historial);
    void eliminar(Long id);
}
