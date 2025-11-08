package com.garritas.sgv.repository;

import com.garritas.sgv.model.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CargoRepository extends JpaRepository<Cargo, Long> {
    Optional<Cargo> findByNombre(String nombre);
}