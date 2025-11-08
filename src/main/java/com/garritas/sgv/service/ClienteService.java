package com.garritas.sgv.service;

import com.garritas.sgv.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> listar();
    Optional<Cliente> buscarPorId(Long id);
    Cliente guardar(Cliente cliente);
    void eliminar(Long id);
}
