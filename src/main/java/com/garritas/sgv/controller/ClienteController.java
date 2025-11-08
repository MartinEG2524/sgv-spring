package com.garritas.sgv.controller;

import com.garritas.sgv.model.Cliente;
import com.garritas.sgv.service.ClienteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Vista de todos los clientes, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping
    public String listar(Model model) {
        List<Cliente> clientes = clienteService.listar();
        model.addAttribute("clientes", clientes);
        return "clientes/listar";
    }

    // Vista para ver un cliente específico, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/{id}")
    public String buscarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id).orElse(null);
        model.addAttribute("cliente", cliente);
        return "clientes/ver";
    }

    // Vista para agregar un nuevo cliente
    @PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/registrar")
    public String registarCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/registrar";
    }

    // Guardar un nuevo cliente
    @PreAuthorize("hasRole('ROLE_CLIENTE') or hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @PostMapping
    public String guardarCliente(@ModelAttribute Cliente cliente) {
        clienteService.guardar(cliente);
        return "redirect:/clientes";
    }

    // Eliminar un cliente, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id) {
        clienteService.eliminar(id);
        return "redirect:/clientes";
    }

    // Vista para editar un cliente existente
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id).orElse(null); // Obtener el cliente
        model.addAttribute("cliente", cliente);  // Pasar los datos al frontend para el formulario
        return "clientes/editar";  // Vista Thymeleaf: clientes/editar.html
    }

    // Guardar los cambios de un cliente editado
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarCliente(@PathVariable Long id, @ModelAttribute Cliente cliente) {
        cliente.setIdCliente(id);
        clienteService.guardar(cliente);
        return "redirect:/clientes";
    }
}
