package com.garritas.sgv.service;

import com.garritas.sgv.model.Cliente;
import com.garritas.sgv.repository.ClienteRepository;
import com.garritas.sgv.util.ValidarEmail;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente guardar(Cliente cliente) {
        if (StringUtils.isBlank(cliente.getNombres())) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (!ValidarEmail.isValid(cliente.getCorreo())) {
                throw new IllegalArgumentException("El email no es válido.");
            }

        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        int actualizar = clienteRepository.actualizarEstado(id, "Inactivo");
        if (actualizar == 0) throw new IllegalArgumentException("Usuario no encontrado: " + id);
    }

    @Override
    public Optional<Cliente> buscarPorDni(Integer dni) {
        return clienteRepository.findByDni(dni);
    }
}
