package com.garritas.sgv.service;

import com.garritas.sgv.model.ColaAtencion;
import com.garritas.sgv.repository.ColaAtencionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ColaAtencionServiceImpl implements ColaAtencionService {

    private final ColaAtencionRepository repository;

    public ColaAtencionServiceImpl(ColaAtencionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ColaAtencion> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ColaAtencion> listarPendientes() {
        return repository.findByFechaIngreso(null);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ColaAtencion> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    public ColaAtencion guardar(ColaAtencion item) {
        if (item.getFechaIngreso() == null) {
            item.setFechaIngreso(LocalDateTime.now());
        }
        if (item.getAtendido() == null) {
            item.setAtendido(false);
        }
        return repository.save(item);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void marcarAtendido(Long id) {
        int updated = repository.marcarAtendido(id);
        if (updated == 0) {
            ColaAtencion c = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("ColaAtencion no encontrada: " + id));
            c.setAtendido(true);
            repository.save(c);
        }
    }
}
