package com.garritas.sgv.service;

import com.garritas.sgv.model.Veterinario;
import com.garritas.sgv.model.Cita;
import com.garritas.sgv.model.HistorialClinico;
import com.garritas.sgv.model.Servicio;
import com.garritas.sgv.repository.VeterinarioRepository;
import com.garritas.sgv.repository.CitaRepository;
import com.garritas.sgv.repository.HistorialClinicoRepository;
import com.garritas.sgv.repository.ServicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeterinarioServiceImpl implements VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final CitaRepository citaRepository;
    private final HistorialClinicoRepository historialClinicoRepository;
    private final ServicioRepository servicioRepository;

    public VeterinarioServiceImpl(VeterinarioRepository veterinarioRepository, CitaRepository citaRepository,
        HistorialClinicoRepository historialClinicoRepository, ServicioRepository servicioRepository) {
        this.veterinarioRepository = veterinarioRepository;
        this.citaRepository = citaRepository;
        this.historialClinicoRepository = historialClinicoRepository;
        this.servicioRepository = servicioRepository;
    }

    @Override
    public List<Veterinario> listar() {
        return veterinarioRepository.findAll();
    }

    @Override
    public Optional<Veterinario> buscarPorId(Long id) {
        return veterinarioRepository.findById(id);
    }

    @Override
    public Veterinario guardar(Veterinario veterinario) {
        return veterinarioRepository.save(veterinario);
    }

    @Override
    public void eliminar(Long id) {
        veterinarioRepository.deleteById(id);
    }

    // Implementación de los métodos de citas, historial y servicios
    @Override
    public List<Cita> obtenerCitas() {
        return citaRepository.findAll();
    }

    @Override
    public List<HistorialClinico> obtenerHistorial() {
        return historialClinicoRepository.findAll();
    }

    @Override
    public List<Servicio> obtenerServicios() {
        return servicioRepository.findAll();
    }
}
