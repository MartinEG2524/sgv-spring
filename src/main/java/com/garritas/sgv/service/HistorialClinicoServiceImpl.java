package com.garritas.sgv.service;

import com.garritas.sgv.model.HistorialClinico;
import com.garritas.sgv.repository.HistorialClinicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HistorialClinicoServiceImpl implements HistorialClinicoService {

    private final HistorialClinicoRepository historialRepository;

    public HistorialClinicoServiceImpl(HistorialClinicoRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    @Override
    public List<HistorialClinico> listar() {
        return historialRepository.findAll();
    }

    @Override
    public Optional<HistorialClinico> buscarPorId(Long id) {
        return historialRepository.findById(id);
    }

    @Override
    public Optional<HistorialClinico> buscarPorCodigo(String codigo) {
        return historialRepository.findByCodigo(codigo);
    }

    @Override
    public HistorialClinico guardar(HistorialClinico historial) {
        historial.setEstado("Activo");
        return historialRepository.save(historial);
    }

    @Override
    public HistorialClinico actualizar(HistorialClinico historial) {
        return historialRepository.save(historial);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        int actualizar = historialRepository.actualizarEstado(id, "Inactivo");
        if (actualizar == 0) throw new IllegalArgumentException("Historial Clinico no encontrado: " + id);
    }
}
