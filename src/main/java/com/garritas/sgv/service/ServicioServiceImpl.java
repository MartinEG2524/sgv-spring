package com.garritas.sgv.service;

import com.garritas.sgv.model.Servicio;
import com.garritas.sgv.repository.ServicioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;

    public ServicioServiceImpl(ServicioRepository servicioRepository) { 
        this.servicioRepository = servicioRepository;
    }

    @Override
    public List<Servicio> listar() {
        return servicioRepository.findAll();
    }

    @Override
    public Optional<Servicio> buscarPorId(Long id) {
        return servicioRepository.findById(id);
    }

    @Override
    public Optional<Servicio> buscarPorDescripcion(String descripcion) {
        return servicioRepository.findByDescripcion(descripcion);
    }

    @Override
    public Servicio guardar(Servicio servicio) {
        servicio.setEstado("Activo");
        return servicioRepository.save(servicio);
    }

    @Override
    public Servicio actualizar(Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        int actualizar = servicioRepository.actualizarEstado(id, "Inactivo");
        if (actualizar == 0) throw new IllegalArgumentException("Servicio no encontrado: " + id);
    }
}
