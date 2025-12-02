package com.garritas.sgv.service;

import com.garritas.sgv.model.Mascota;

import java.util.List;
import java.util.Optional;

public interface MascotaService {
    List<Mascota> listar();
    Optional<Mascota> buscarPorId(Long id);
    Optional<Mascota> buscarPorDni(Integer dni);
    Optional<Mascota> buscarPorCodigo(String codigo);
    Mascota guardar(Mascota mascota);
    Mascota actualizar(Mascota mascota);
    void eliminar(Long id);
}
