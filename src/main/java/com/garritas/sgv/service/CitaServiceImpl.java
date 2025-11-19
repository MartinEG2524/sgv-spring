package com.garritas.sgv.service;

import com.garritas.sgv.model.Cita;
import com.garritas.sgv.repository.CitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
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

    public Cita guardar(Cita cita) {
        if (cita.getFecha() == null) {
            throw new IllegalArgumentException("La fecha no puede estar vacía");
        }

        if (cita.getHora() == null) {
            throw new IllegalArgumentException("La hora no puede estar vacía");
        }

        if (!isValidTimeFormat(cita.getHora())) {
            throw new IllegalArgumentException("La hora no tiene un formato válido. Formato: HH:mm");
        }
        cita.setEstado("Activo");
        return citaRepository.save(cita);
    }

    private boolean isValidTimeFormat(LocalTime hora) {
        String regex = "([01]?[0-9]|2[0-3]):([0-5]?[0-9])";  
        String horaStr = hora.toString(); 
        return horaStr.matches(regex);
    }

    public Cita actualizar(Cita cita) {
        return citaRepository.save(cita);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        int actualizar = citaRepository.actualizarEstado(id, "Inactivo");
        if (actualizar == 0) throw new IllegalArgumentException("Cita no encontrada: " + id);
    }

    public boolean existeCitaEnHorario(LocalDate fecha, LocalTime hora) {
        String horaStr = hora.toString();
        return citaRepository.existsCitaEnHorario(fecha, horaStr) > 0;
    }
}
