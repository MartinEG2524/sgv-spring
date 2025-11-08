package com.garritas.sgv.service;

import com.garritas.sgv.model.Veterinario;
import com.garritas.sgv.model.Cita;
import com.garritas.sgv.model.HistorialClinico;
import com.garritas.sgv.model.Servicio;

import java.util.List;
import java.util.Optional;

public interface VeterinarioService {
    List<Veterinario> listar();
    Optional<Veterinario> buscarPorId(Long id);
    Veterinario guardar(Veterinario veterinario);
    void eliminar(Long id);
    
    // Métodos para obtener citas, historial y servicios
    List<Cita> obtenerCitas();
    List<HistorialClinico> obtenerHistorial();
    List<Servicio> obtenerServicios();
}
