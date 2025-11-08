package com.garritas.sgv.service;

import com.garritas.sgv.model.Cargo;

import java.util.List;
import java.util.Optional;

public interface CargoService {
    List<Cargo> listar();
    Optional<Cargo> buscarPorId(Long id);
    Cargo guardar(Cargo cargo);
    void eliminar(Long id);
}
