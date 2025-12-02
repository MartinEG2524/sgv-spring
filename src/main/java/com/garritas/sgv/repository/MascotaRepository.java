package com.garritas.sgv.repository;

import com.garritas.sgv.model.Mascota;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    Optional<Mascota> findByDni(Integer dni);
    Optional<Mascota> findByCodigo(String codigo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Mascota m SET m.estado = :estado WHERE m.idMascota = :id")
    Integer actualizarEstado(@Param("id") Long id, @Param("estado") String estado);
}
