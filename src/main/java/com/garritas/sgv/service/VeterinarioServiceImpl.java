package com.garritas.sgv.service;

import com.garritas.sgv.model.Veterinario;
import com.garritas.sgv.repository.VeterinarioRepository;
import com.garritas.sgv.util.ValidarEmail;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

@Service
public class VeterinarioServiceImpl implements VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;

    public VeterinarioServiceImpl(VeterinarioRepository veterinarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
    }

    @Override
    public List<Veterinario> listar() {
        return veterinarioRepository.findAll();
    }

    @Override
    public Optional<Veterinario> buscarPorId(Long id) {
        return veterinarioRepository.findById(id);
    }

    public Veterinario guardar(Veterinario cliente) {
        if (StringUtils.isEmpty(cliente.getNombres())) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        
        if (!ValidarEmail.isValid(cliente.getCorreo())) {
            throw new IllegalArgumentException("El email no es válido.");
        }

        return veterinarioRepository.save(cliente);
    }

    public Veterinario actualizar(Veterinario veterinario) {
        return veterinarioRepository.save(veterinario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        int actualizar = veterinarioRepository.actualizarEstado(id, "Inactivo");
        if (actualizar == 0) throw new IllegalArgumentException("veterinario no encontrado: " + id);
    }

    @Override
    public Optional<Veterinario> buscarPorDni(Integer dni) {
        return veterinarioRepository.findByDni(dni);
    }
}
