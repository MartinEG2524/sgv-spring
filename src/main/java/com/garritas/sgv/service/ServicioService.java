package com.garritas.sgv.service;

import com.garritas.sgv.model.Servicio;

import java.util.List;
import java.util.Optional;

public interface ServicioService {
    List<Servicio> listar();
    Optional<Servicio> buscarPorId(Long id);
    Optional<Servicio> buscarPorDescripcion(String descripcion);
    Servicio guardar(Servicio servicio);
    Servicio actualizar(Servicio servicio);
    void eliminar(Long id);
}
