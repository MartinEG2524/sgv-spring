package com.garritas.sgv.controller;

import com.garritas.sgv.model.Mascota;
import com.garritas.sgv.service.MascotaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    // Vista de todos las mascotas, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping
    public String listar(Model model) {
        List<Mascota> mascotas = mascotaService.listar();
        model.addAttribute("mascotas", mascotas);
        return "mascotas/listar";
    }

    // Vista para ver una mascota específica, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/{id}")
    public String buscarMascota(@PathVariable Long id, Model model) {
        Mascota mascota = mascotaService.buscarPorId(id).orElse(null);
        model.addAttribute("mascota", mascota);
        return "mascotas/ver";
    }

    // Vista para agregar una nueva mascota, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarMascota(Model model) {
        model.addAttribute("mascota", new Mascota());
        return "mascotas/registrar";
    }

    // Guardar una nueva mascota, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public String guardarMascota(@ModelAttribute Mascota mascota) {
        mascotaService.guardar(mascota);
        return "redirect:/mascotas";
    }

    // Eliminar una mascota, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminarMascota(@PathVariable Long id) {
        mascotaService.eliminar(id);
        return "redirect:/mascotas";
    }

    // Vista para editar una mascota existente, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarMascota(@PathVariable Long id, Model model) {
        Mascota mascota = mascotaService.buscarPorId(id).orElse(null);
        model.addAttribute("mascota", mascota);
        return "mascotas/editar";
    }

    // Guardar los cambios de una mascota editada, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarMascota(@PathVariable Long id, @ModelAttribute Mascota mascota) {
        mascota.setIdMascota(id);
        mascotaService.guardar(mascota);
        return "redirect:/mascotas";
    }
}
