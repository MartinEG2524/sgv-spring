package com.garritas.sgv.controller;

import com.garritas.sgv.model.HistorialClinico;
import com.garritas.sgv.service.HistorialClinicoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/historiales")
public class HistorialClinicoController {

    private final HistorialClinicoService service;

    public HistorialClinicoController(HistorialClinicoService service) {
        this.service = service;
    }

    // Vista de todos los historiales clínicos, solo accesible para ADMIN y VETERINARIO
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VETERINARIO')")
    @GetMapping
    public String listar(Model model) {
        List<HistorialClinico> historiales = service.listar();
        model.addAttribute("historiales", historiales);
        return "historiales/listar";
    }

    // Vista para ver un historial clínico específico, solo accesible para ADMIN y VETERINARIO
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VETERINARIO')")
    @GetMapping("/{id}")
    public String buscarHistorial(@PathVariable Long id, Model model) {
        HistorialClinico historial = service.buscarPorId(id).orElse(null);
        model.addAttribute("historial", historial);
        return "historiales/ver";
    }

    // Vista para agregar un nuevo historial clínico, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarHistorial(Model model) {
        model.addAttribute("historial", new HistorialClinico());
        return "historiales/registrar";
    }

    // Guardar un nuevo historial clínico, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public String guardarHistorial(@ModelAttribute HistorialClinico historial) {
        service.guardar(historial);  // Guardar el historial clínico
        return "redirect:/historiales";
    }

    // Eliminar un historial clínico, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminarHistorial(@PathVariable Long id) {
        service.eliminar(id);
        return "redirect:/historiales";
    }

    // Vista para editar un historial clínico existente, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarHistorial(@PathVariable Long id, Model model) {
        HistorialClinico historial = service.buscarPorId(id).orElse(null);
        model.addAttribute("historial", historial);
        return "historiales/editar";
    }

    // Guardar los cambios de un historial clínico editado, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarHistorial(@PathVariable Long id, @ModelAttribute HistorialClinico historial) {
        historial.setIdHistorial(id);
        service.guardar(historial);
        return "redirect:/historiales";
    }
}
