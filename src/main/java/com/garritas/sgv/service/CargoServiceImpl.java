package com.garritas.sgv.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.garritas.sgv.model.Cargo;
import com.garritas.sgv.repository.CargoRepository;

@Service
public class CargoServiceImpl implements CargoService {

    private final CargoRepository cargoRepository;

    public CargoServiceImpl(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    @Override
    public List<Cargo> listar() {
        return cargoRepository.findAll();
    }

    @Override
    public Optional<Cargo> buscarPorId(Long id) {
        return cargoRepository.findById(id);
    }

    @Override
    public Cargo guardar(Cargo Cargo) {
        return cargoRepository.save(Cargo);
    }

    public Cargo actualizar(Cargo Cargo) {
        return cargoRepository.save(Cargo);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        int actualizar = cargoRepository.actualizarEstado(id, "Inactivo");
        if (actualizar == 0) throw new IllegalArgumentException("Cargo no encontrado: " + id);
    }

    @Override
    public Optional<Cargo> buscarPorNombre(String nombre) {
        return cargoRepository.findByNombre(nombre);
    }
}
