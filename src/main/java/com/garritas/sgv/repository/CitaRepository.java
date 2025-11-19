package com.garritas.sgv.repository;

import com.garritas.sgv.model.Cita;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Cita u SET u.estado = :estado WHERE u.idCita = :id")
    Integer actualizarEstado(@Param("id") Long id, @Param("estado") String estado);

    @Query(value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END
        FROM citas c
        WHERE c.fecha = :fecha AND c.hora = CAST(:hora AS time)
        """,
        nativeQuery = true)
    int existsCitaEnHorario(@Param("fecha") LocalDate fecha, @Param("hora") String hora);
}
