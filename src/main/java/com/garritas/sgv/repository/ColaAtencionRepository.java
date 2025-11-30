package com.garritas.sgv.repository;

import com.garritas.sgv.model.ColaAtencion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ColaAtencionRepository extends JpaRepository<ColaAtencion, Long> {

    @EntityGraph(attributePaths = "mascota")
    List<ColaAtencion> findByFechaIngreso(LocalDateTime fechaIngreso);

    Optional<ColaAtencion> findFirstByFechaIngreso(LocalDateTime fechaIngreso);

    List<ColaAtencion> findByFechaIngresoAndAtendidoFalse(LocalDateTime fechaIngreso);

    @Modifying
    @Query("update ColaAtencion c set c.atendido = true where c.idCola = :id")
    int marcarAtendido(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ColaAtencion c SET c.estado = :estado WHERE c.idCola = :id")
    Integer actualizarEstado(@Param("id") Long id, @Param("estado") String estado);
}
