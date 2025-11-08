package com.garritas.sgv.service;

import com.garritas.sgv.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> listar();
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorCodigo(String codigo);
    Usuario guardar(Usuario usuario);
    void eliminar(Long id);
    boolean actualizarContrasena(String codigo, String nuevaContrasena);
    List<Usuario> buscarUsuario(Integer IdRol, Integer IdUsuario);
}
