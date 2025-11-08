package com.garritas.sgv.repository;

import com.garritas.sgv.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCodigo(String codigo);

    @Procedure
    List<Object[]> sp_buscar_usuario(@Param("IdRol") Integer IdRol, @Param("IdUsuario") Integer IdUsuario);
}
