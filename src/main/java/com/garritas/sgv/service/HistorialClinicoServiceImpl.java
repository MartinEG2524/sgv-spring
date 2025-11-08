package com.garritas.sgv.service;

import com.garritas.sgv.model.HistorialClinico;
import com.garritas.sgv.repository.HistorialClinicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistorialClinicoServiceImpl implements HistorialClinicoService {

    private final HistorialClinicoRepository repository;

    public HistorialClinicoServiceImpl(HistorialClinicoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<HistorialClinico> listar() {
        return repository.findAll();
    }

    @Override
    public Optional<HistorialClinico> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public HistorialClinico guardar(HistorialClinico historial) {
        return repository.save(historial);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
