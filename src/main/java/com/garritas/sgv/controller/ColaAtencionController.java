package com.garritas.sgv.controller;

import com.garritas.sgv.model.ColaAtencion;
import com.garritas.sgv.model.Mascota;
import com.garritas.sgv.service.ColaAtencionService;
import com.garritas.sgv.service.MascotaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cola")
public class ColaAtencionController {

    private final ColaAtencionService colaService;
    private final MascotaService mascotaService;

    public ColaAtencionController(ColaAtencionService colaService, MascotaService mascotaService) {
        this.colaService = colaService;
        this.mascotaService = mascotaService;
    }

    // Listado general (Toda la cola) - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping
    public String listar(Model model) {
        List<ColaAtencion> cola = colaService.listar();
        model.addAttribute("cola", cola);
        return "colas/listar";
    }

    // Listado de pendientes (No atendidos) - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/pendientes")
    public String listarPendientes(Model model) {
        List<ColaAtencion> pendientes = colaService.listarPendientes();
        model.addAttribute("cola", pendientes);
        return "colas/pendientes";
    }

    // Ver un registro de cola - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        ColaAtencion item = colaService.buscarPorId(id).orElse(null);
        model.addAttribute("item", item);
        return "colas/ver";
    }

    // Registrar ingreso de cola - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/registrar")
    public String registrar(Model model) {
        model.addAttribute("item", new ColaAtencion());
        // Listar combo de mascotas
        List<Mascota> mascotas = mascotaService.listar();
        model.addAttribute("mascotas", mascotas);
        return "colas/registrar";
    }

    // Guardar nuevo ingreso - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @PostMapping
    public String guardar(@ModelAttribute("item") ColaAtencion item) {
        colaService.guardar(item);
        return "redirect:/colas/pendientes";
    }

    // Eliminar un registro - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        colaService.eliminar(id);
        return "redirect:/colas";
    }

    // Editar un registro existente - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        ColaAtencion item = colaService.buscarPorId(id).orElse(null);
        model.addAttribute("item", item);
        // Cargar combo de mascotas
        model.addAttribute("mascotas", mascotaService.listar());
        return "colas/editar";
    }

    // Actualizar registro - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute("item") ColaAtencion item) {
        item.setIdCola(id);
        colaService.guardar(item);
        return "redirect:/colas";
    }

    // Marcar como atendido - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @PostMapping("/marcar-atendido/{id}")
    public String marcarAtendido(@PathVariable Long id) {
        colaService.marcarAtendido(id);
        return "redirect:/colas/pendientes";
    }
}
