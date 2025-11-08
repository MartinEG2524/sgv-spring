package com.garritas.sgv.controller;

import com.garritas.sgv.model.DetalleHistorialInventario;
import com.garritas.sgv.service.DetalleHistorialInventarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/detalles")
public class DetalleHistorialInventarioController {

    private final DetalleHistorialInventarioService service;

    public DetalleHistorialInventarioController(DetalleHistorialInventarioService service) {
        this.service = service;
    }

    // Vista de todos los detalles del historial de inventario, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping
    public String listar(Model model) {
        List<DetalleHistorialInventario> detalles = service.listar();
        model.addAttribute("detalles", detalles);
        return "detalles/listar";
    }

    // Vista para ver un detalle específico, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/{id}")
    public String buscarDetalle(@PathVariable Long id, Model model) {
        DetalleHistorialInventario detalle = service.buscarPorId(id).orElse(null);
        model.addAttribute("detalle", detalle);
        return "detalles/ver";
    }

    // Vista para agregar un nuevo detalle
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registar")
    public String registarDetalle(Model model) {
        model.addAttribute("detalle", new DetalleHistorialInventario());
        return "detalles/registar";
    }

    // Guardar un nuevo detalle
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public String guardarDetalle(@ModelAttribute DetalleHistorialInventario detalle) {
        service.guardar(detalle);
        return "redirect:/detalles";
    }

    // Eliminar un detalle, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminarDetalle(@PathVariable Long id) {
        service.eliminar(id);
        return "redirect:/detalles";
    }

    // Vista para editar un detalle existente, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarDetalle(@PathVariable Long id, Model model) {
        DetalleHistorialInventario detalle = service.buscarPorId(id).orElse(null);
        model.addAttribute("detalle", detalle);
        return "detalles/editar";
    }

    // Guardar los cambios de un detalle editado, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarDetalle(@PathVariable Long id, @ModelAttribute DetalleHistorialInventario detalle) {
        detalle.setIdDetalle(id);
        service.guardar(detalle);
        return "redirect:/detalles";
    }
}
