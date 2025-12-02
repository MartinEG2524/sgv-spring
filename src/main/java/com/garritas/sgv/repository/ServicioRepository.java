package com.garritas.sgv.repository;

import com.garritas.sgv.model.Servicio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    Optional<Servicio> findByDescripcion(String descripcion);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Servicio s SET s.estado = :estado WHERE s.idServicio = :id")
    Integer actualizarEstado(@Param("id") Long id, @Param("estado") String estado);
}
