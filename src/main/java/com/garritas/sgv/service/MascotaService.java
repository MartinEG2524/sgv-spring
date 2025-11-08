package com.garritas.sgv.service;

import com.garritas.sgv.model.Mascota;

import java.util.List;
import java.util.Optional;

public interface MascotaService {
    List<Mascota> listar();
    Optional<Mascota> buscarPorId(Long id);
    Mascota guardar(Mascota mascota);
    void eliminar(Long id);
}
