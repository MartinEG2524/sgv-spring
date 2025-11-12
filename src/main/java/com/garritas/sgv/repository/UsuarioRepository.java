package com.garritas.sgv.repository;

import com.garritas.sgv.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCodigo(String codigo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Usuario u SET u.estado = :estado WHERE u.idUsuario = :id")
    int actualizarEstado(@Param("id") Long id, @Param("estado") String estado);

    @Procedure
    List<Object[]> sp_buscar_usuario(@Param("IdRol") Integer IdRol, @Param("IdUsuario") Integer IdUsuario);
}
