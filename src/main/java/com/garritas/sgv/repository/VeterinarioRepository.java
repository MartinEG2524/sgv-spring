package com.garritas.sgv.repository;

import com.garritas.sgv.model.Veterinario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Veterinario u SET u.estado = :estado WHERE u.idVeterinario = :id")
    Integer actualizarEstado(@Param("id") Long id, @Param("estado") String estado);

    Optional<Veterinario> findByDni(Integer dni);

}
