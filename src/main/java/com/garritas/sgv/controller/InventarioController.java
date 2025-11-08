package com.garritas.sgv.controller;

import com.garritas.sgv.model.Inventario;
import com.garritas.sgv.service.InventarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/inventarios")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    // Vista de todos los inventarios, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping
    public String listar(Model model) {
        List<Inventario> inventarios = inventarioService.listar();
        model.addAttribute("inventarios", inventarios);
        return "inventarios/listar";
    }

    // Vista para ver un inventario específico, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/{id}")
    public String buscarInventario(@PathVariable Long id, Model model) {
        Inventario inventario = inventarioService.buscarPorId(id).orElse(null);
        model.addAttribute("inventario", inventario);
        return "inventarios/ver";
    }

    // Vista para agregar un nuevo inventario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarInventario(Model model) {
        model.addAttribute("inventario", new Inventario());
        return "inventarios/registrar";
    }

    // Guardar un nuevo inventario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public String guardarInventario(@ModelAttribute Inventario inventario) {
        inventarioService.guardar(inventario);
        return "redirect:/inventarios";
    }

    // Eliminar un inventario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminarInventario(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return "redirect:/inventarios";
    }

    // Vista para editar un inventario existente, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarInventario(@PathVariable Long id, Model model) {
        Inventario inventario = inventarioService.buscarPorId(id).orElse(null);
        model.addAttribute("inventario", inventario);
        return "inventarios/editar";
    }

    // Guardar los cambios de un inventario editado, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarInventario(@PathVariable Long id, @ModelAttribute Inventario inventario) {
        inventario.setIdProducto(id);
        inventarioService.guardar(inventario);
        return "redirect:/inventarios";
    }
}
