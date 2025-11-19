package com.garritas.sgv.service;

import com.garritas.sgv.model.Cita;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface CitaService {
    List<Cita> listar();
    Optional<Cita> buscarPorId(Long id);
    boolean existeCitaEnHorario(LocalDate fecha, LocalTime hora);
    Cita guardar(Cita cita);
    Cita actualizar(Cita cita);
    void eliminar(Long id);
}
