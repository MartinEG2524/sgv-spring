package com.garritas.sgv.service;

import com.garritas.sgv.model.Cargo;
import com.garritas.sgv.model.Cliente;
import com.garritas.sgv.model.Usuario;
import com.garritas.sgv.model.Veterinario;
import com.garritas.sgv.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> buscarPorCodigo(String codigo) {
        return usuarioRepository.findByCodigo(codigo);
    }

    public Usuario guardar(Usuario usuario) {
        String encryptedPassword = passwordEncoder.encode(usuario.getContrasena());
        log.info("Registrando usuario con código: ", usuario.getCodigo());
        usuario.setContrasena(encryptedPassword);
        usuario.setEstado("Activo");
        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public boolean actualizarContrasena(String codigo, String nuevaContrasena) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByCodigo(codigo);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            String encryptedPassword = passwordEncoder.encode(nuevaContrasena);
            usuario.setContrasena(encryptedPassword);
            usuarioRepository.save(usuario);
            log.info("Contraseña actualizada del usuario con código: " + codigo);
            return true;
        }

        log.error("No se encontró el usuario con el código: " + codigo);
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarUsuario(Integer IdRol, Integer IdUsuario) {
        List<Object[]> resultados = usuarioRepository.sp_buscar_usuario(IdRol, IdUsuario);
        List<Usuario> usuarios = new ArrayList<>();

        for (Object[] row : resultados) {
            Usuario usuario = new Usuario();
            
            usuario.setIdUsuario((Long) row[0]);
            usuario.setCodigo((String) row[1]);

            Cargo cargo = new Cargo();
            cargo.setNombre((String) row[2]);
        
            usuario.setIdCargo(cargo);
            
            if (IdRol == 2 || IdRol == 3) {
                Veterinario veterinario = new Veterinario();
                veterinario.setIdVeterinario((Long) row[3]);
                veterinario.setNombres((String) row[4]);
                veterinario.setApellidos((String) row[5]);
                veterinario.setDni((Integer) row[6]);
                veterinario.setCorreo((String) row[7]);
                veterinario.setEspecialidad((String) row[8]);
                veterinario.setSexo((String) row[9]);
                veterinario.setFechaNacimiento((Date) row[10]);
                veterinario.setCelular((Integer) row[11]);
                veterinario.setPais((String) row[12]);
                veterinario.setProvincia((String) row[13]);
                veterinario.setDistrito((String) row[14]);
                usuario.setVeterinario(veterinario);
            }
            else if (IdRol == 4) {
                Cliente cliente = new Cliente();
                cliente.setIdCliente((Long) row[3]);
                cliente.setNombres((String) row[4]);
                cliente.setApellidos((String) row[5]);
                cliente.setDni((Integer) row[6]);
                cliente.setCorreo((String) row[7]);
                cliente.setDireccion((String) row[8]);
                cliente.setSexo((String) row[9]);
                cliente.setFechaNacimiento((Date) row[10]);
                cliente.setCelular((Integer) row[11]);
                cliente.setPais((String) row[12]);
                cliente.setProvincia((String) row[13]);
                cliente.setDistrito((String) row[14]);
                usuario.setCliente(cliente);
            }
            usuarios.add(usuario);
        }

        return usuarios;
    }
}
