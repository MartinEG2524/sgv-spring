package com.garritas.sgv.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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

    @Override
    public void eliminar(Long id) {
        cargoRepository.deleteById(id);
    }
}
