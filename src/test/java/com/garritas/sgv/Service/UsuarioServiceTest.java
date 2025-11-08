package com.garritas.sgv.Service;

import com.garritas.sgv.model.Usuario;
import com.garritas.sgv.repository.UsuarioRepository;
import com.garritas.sgv.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    private UsuarioRepository usuarioRepository;

    @Test
    public void testBuscarUsuarioPorCodigo() {
        // Crear un usuario ficticio
        Usuario usuario = new Usuario();
        usuario.setCodigo("testuser");
        usuario.setContrasena("password");
        usuario.setIdCargo(null);

        // Simular la respuesta del repositorio
        when(usuarioRepository.findByCodigo("testuser")).thenReturn(Optional.of(usuario));

        // Llamar al servicio
        Optional<Usuario> result = usuarioService.buscarPorCodigo("testuser");

        // Verificar que el usuario existe
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getCodigo());
    }
}
