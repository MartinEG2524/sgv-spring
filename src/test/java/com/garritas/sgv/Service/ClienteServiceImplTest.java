package com.garritas.sgv.Service;

import com.garritas.sgv.model.Cliente;
import com.garritas.sgv.repository.ClienteRepository;
import com.garritas.sgv.service.ClienteServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository; // Fingimos la base de datos

    @InjectMocks
    private ClienteServiceImpl clienteService; // Probamos tu servicio real

    @Test
    public void testListarClientes() {
        // --- 1. PREPARAR DATOS FALSOS ---
        Cliente c1 = new Cliente();
        c1.setNombres("Juan Perez");
        Cliente c2 = new Cliente();
        c2.setNombres("Maria Gomez");

        // Le decimos al "actor" (Mock) que cuando le pidan buscar, devuelva estos 2 clientes
        when(clienteRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        // --- 2. EJECUTAR EL SERVICIO ---
        System.out.println("===========================================");
        System.out.println("PRUEBA UNITARIA: ClienteService -> Listar");
        System.out.println("===========================================");

        List<Cliente> resultado = clienteService.listar();

        // --- 3. VERIFICAR EN CONSOLA ---
        System.out.println("Resultado esperado: 2 clientes");
        System.out.println("Resultado obtenido: " + resultado.size() + " clientes");

        // Esta línea es la que decide si la prueba pasa (verde) o falla (rojo)
        assertEquals(2, resultado.size());

        System.out.println("¡PRUEBA EXITOSA! La lógica funciona.");
    }
}