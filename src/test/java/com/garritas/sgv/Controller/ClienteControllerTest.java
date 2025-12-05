package com.garritas.sgv.Controller;

import com.garritas.sgv.controller.ClienteController;
import com.garritas.sgv.model.Cliente;
import com.garritas.sgv.repository.CargoRepository;
import com.garritas.sgv.repository.UsuarioRepository;
import com.garritas.sgv.service.ClienteService;
import com.garritas.sgv.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private CargoRepository cargoRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void testVerPaginaListar() throws Exception {
        // GIVEN: Preparamos los datos
        // 1. Creamos un Usuario falso
        com.garritas.sgv.model.Usuario u = new com.garritas.sgv.model.Usuario();
        u.setIdUsuario(1L);
        u.setCodigo("US01");

        // 2. Creamos el Cliente con su Usuario
        Cliente c = new Cliente();
        c.setNombres("Usuario Test");
        c.setUsuario(u);

        // Simulamos que el servicio devuelve ese cliente
        when(clienteService.listar()).thenReturn(Arrays.asList(c));

        // WHEN & THEN
        System.out.println("====== [TEST CONTROLLER] VER LISTA ======");

        // CORRECCIÓN AQUÍ: Agregamos un Token CSRF falso (.requestAttr)
        mockMvc.perform(get("/clientes/listar")
                .requestAttr("_csrf",
                        new org.springframework.security.web.csrf.DefaultCsrfToken("X-CSRF-TOKEN", "_csrf",
                                "token-falso-123")))
                .andDo(result -> {
                    System.out.println(" -> Petición GET a: /clientes/listar");
                    System.out.println(" -> Estado HTTP: " + result.getResponse().getStatus());
                })
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/listar"))
                .andExpect(model().attributeExists("clientes"));
    }

    @Test
    public void testRegistrarCliente_Redireccion() throws Exception {
        // GIVEN
        when(clienteService.buscarPorDni(any(Integer.class))).thenReturn(Optional.empty());

        // WHEN & THEN
        System.out.println("====== [TEST CONTROLLER] REGISTRAR POST ======");

        mockMvc.perform(post("/clientes/registrar")
                .param("nombres", "Nuevo")
                .param("apellidos", "Cliente")
                .param("dni", "99999999")
                .param("usuario.idUsuario", "10"))
                .andDo(result -> {
                    System.out.println(" -> Redirige a: " + result.getResponse().getRedirectedUrl());
                })
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes/listar"));
    }
}