package com.garritas.sgv.service;

import com.garritas.sgv.model.ColaAtencion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ColaAtencionService {
    List<ColaAtencion> listar();
    List<ColaAtencion> listarPendientes();
    List<ColaAtencion> listarPorFechaNoAtendidas();
    Optional<ColaAtencion> buscarPorId(Long id);
    Optional<ColaAtencion> buscarPorFechaIngreso(LocalDateTime fechaIngreso);
    ColaAtencion guardar(ColaAtencion colaAtencion);
    void eliminar(Long id);
    void marcarAtendido(Long id);
}
