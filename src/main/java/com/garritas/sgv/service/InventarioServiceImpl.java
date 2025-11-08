package com.garritas.sgv.service;

import com.garritas.sgv.model.Inventario;
import com.garritas.sgv.repository.InventarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioServiceImpl(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    public List<Inventario> listar() {
        return inventarioRepository.findAll();
    }

    @Override
    public Optional<Inventario> buscarPorId(Long id) {
        return inventarioRepository.findById(id);
    }

    @Override
    public Inventario guardar(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    @Override
    public void eliminar(Long id) {
        inventarioRepository.deleteById(id);
    }
}
