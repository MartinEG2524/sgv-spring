package com.garritas.sgv.repository;

import com.garritas.sgv.model.HistorialClinico;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE HistorialClinico h SET h.estado = :estado WHERE h.idHistorial = :id")
    Integer actualizarEstado(@Param("id") Long id, @Param("estado") String estado);

    Optional<HistorialClinico> findByCodigo(String codigo);
}
