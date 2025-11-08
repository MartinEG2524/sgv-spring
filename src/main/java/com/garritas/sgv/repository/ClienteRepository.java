package com.garritas.sgv.repository;

import com.garritas.sgv.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByDni(Integer dni);
}
