package com.garritas.sgv.service;

import com.garritas.sgv.model.DetalleHistorialInventario;
import com.garritas.sgv.repository.DetalleHistorialInventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleHistorialInventarioServiceImpl implements DetalleHistorialInventarioService {

    private final DetalleHistorialInventarioRepository repository;

    public DetalleHistorialInventarioServiceImpl(DetalleHistorialInventarioRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<DetalleHistorialInventario> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<DetalleHistorialInventario> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public DetalleHistorialInventario guardar(DetalleHistorialInventario detalle) {
        return repository.save(detalle);
    }

    @Transactional(readOnly = true)
    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
