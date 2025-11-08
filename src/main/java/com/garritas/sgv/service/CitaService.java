package com.garritas.sgv.service;

import com.garritas.sgv.model.Cita;

import java.util.List;
import java.util.Optional;

public interface CitaService {
    List<Cita> listar();
    Optional<Cita> buscarPorId(Long id);
    Cita guardar(Cita cita);
    void eliminar(Long id);
}
