package com.garritas.sgv.service;

import com.garritas.sgv.model.Mascota;
import com.garritas.sgv.repository.MascotaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MascotaServiceImpl implements MascotaService {

    private final MascotaRepository mascotaRepository;

    public MascotaServiceImpl(MascotaRepository mascotaRepository) {
        this.mascotaRepository = mascotaRepository;
    }

    @Override
    public List<Mascota> listar() {
        return mascotaRepository.findAll();
    }

    @Override
    public Optional<Mascota> buscarPorId(Long id) {
        return mascotaRepository.findById(id);
    }

    @Override
    public Mascota guardar(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    @Override
    public Mascota actualizar(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        int actualizar = mascotaRepository.actualizarEstado(id, "Inactivo");
        if (actualizar == 0) throw new IllegalArgumentException("Mascota no encontrado: " + id);
    }

    @Override
    public Optional<Mascota> buscarPorDni(Integer dni) {
        return mascotaRepository.findByDni(dni);
    }

    @Override
    public Optional<Mascota> buscarPorCodigo(String codigo) {
        return mascotaRepository.findByCodigo(codigo);
    }
}
