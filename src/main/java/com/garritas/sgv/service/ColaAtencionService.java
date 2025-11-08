package com.garritas.sgv.service;

import com.garritas.sgv.model.ColaAtencion;

import java.util.List;
import java.util.Optional;

public interface ColaAtencionService {

    List<ColaAtencion> listar();

    List<ColaAtencion> listarPendientes();

    Optional<ColaAtencion> buscarPorId(Long id);

    ColaAtencion guardar(ColaAtencion item);

    void eliminar(Long id);

    void marcarAtendido(Long id);
}
