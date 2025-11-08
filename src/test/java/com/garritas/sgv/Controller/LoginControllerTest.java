package com.garritas.sgv.Controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser // Esto simula un usuario autenticado en la prueba
    public void testLoginPage_withErrorParam() throws Exception {
        // Simula una petición GET a la página de login con el parámetro error = true
        mockMvc.perform(MockMvcRequestBuilders.get("/login")
                .param("error", "true")
                .param("username", "testuser")
                .param("password", "password"))
                .andExpect(MockMvcResultMatchers.status().isOk())  // Verifica que la respuesta sea exitosa
                .andExpect(MockMvcResultMatchers.view().name("login"))  // Verifica que la vista sea 'login'
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage", "Invalid username or password. Please try again.")) // Verifica que el mensaje de error esté presente
                .andExpect(MockMvcResultMatchers.model().attribute("username", "testuser"))  // Verifica que el 'username' esté en el modelo
                .andExpect(MockMvcResultMatchers.model().attribute("password", "password"));  // Verifica que el 'password' esté en el modelo
    }
}
