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

    private final ColaAtencionRepository colaAtencionRepository;

    public ColaAtencionServiceImpl(ColaAtencionRepository colaAtencionRepository) {
        this.colaAtencionRepository = colaAtencionRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ColaAtencion> listar() {
        return colaAtencionRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ColaAtencion> listarPendientes() {
        return colaAtencionRepository.findByFechaIngreso(null);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ColaAtencion> listarPorFechaNoAtendidas() {
        return colaAtencionRepository.findByFechaIngresoAndAtendidoFalse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ColaAtencion> buscarPorId(Long id) {
        return colaAtencionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ColaAtencion> buscarPorFechaIngreso(LocalDateTime fechaIngreso) {
        return colaAtencionRepository.findFirstByFechaIngreso(fechaIngreso);
    }

    @Override
    public ColaAtencion guardar(ColaAtencion item) {
        if (item.getFechaIngreso() == null) {
            item.setFechaIngreso(LocalDateTime.now());
        }
        if (item.getAtendido() == null) {
            item.setAtendido(false);
        }
        return colaAtencionRepository.save(item);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        int actualizar = colaAtencionRepository.actualizarEstado(id, "Inactivo");
        if (actualizar == 0) throw new IllegalArgumentException("Cola Atención no encontrado: " + id);
    }

    @Override
    public void marcarAtendido(Long id) {
        int actualizar = colaAtencionRepository.marcarAtendido(id);
        if (actualizar == 0) {
            ColaAtencion colaAtencion = colaAtencionRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Cola Atención no encontrado: " + id));
            colaAtencion.setAtendido(true);
            colaAtencionRepository.save(colaAtencion);
        }
    }
}
