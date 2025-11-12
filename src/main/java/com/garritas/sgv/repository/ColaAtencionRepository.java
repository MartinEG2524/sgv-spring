package com.garritas.sgv.repository;

import com.garritas.sgv.model.ColaAtencion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ColaAtencionRepository extends JpaRepository<ColaAtencion, Long> {

    @EntityGraph(attributePaths = "mascota")
    List<ColaAtencion> findByFechaIngreso(LocalDateTime fechaIngreso);

    @Modifying
    @Query("update ColaAtencion c set c.atendido = true where c.idCola = :id")
    int marcarAtendido(Long id);
}
