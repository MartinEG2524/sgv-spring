package com.garritas.sgv.repository;

import com.garritas.sgv.model.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CargoRepository extends JpaRepository<Cargo, Long> {
    Optional<Cargo> findByNombre(String nombre);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Cargo c SET c.estado = :estado WHERE c.idRol = :id")
    Integer actualizarEstado(@Param("id") Long id, @Param("estado") String estado);
}