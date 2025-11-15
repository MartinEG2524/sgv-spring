package com.garritas.sgv.repository;

import com.garritas.sgv.model.Cliente;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Cliente u SET u.estado = :estado WHERE u.idCliente = :id")
    Integer actualizarEstado(@Param("id") Long id, @Param("estado") String estado);

    Optional<Cliente> findByDni(Integer dni);
}
