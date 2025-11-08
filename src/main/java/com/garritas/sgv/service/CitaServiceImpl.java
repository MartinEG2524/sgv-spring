package com.garritas.sgv.service;

import com.garritas.sgv.model.Cita;
import com.garritas.sgv.repository.CitaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;

    public CitaServiceImpl(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @Override
    public List<Cita> listar() {
        return citaRepository.findAll();
    }

    @Override
    public Optional<Cita> buscarPorId(Long id) {
        return citaRepository.findById(id);
    }

    @Override
    public Cita guardar(Cita cita) {
        return citaRepository.save(cita);
    }

    @Override
    public void eliminar(Long id) {
        citaRepository.deleteById(id);
    }
}
