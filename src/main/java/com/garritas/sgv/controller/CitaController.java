package com.garritas.sgv.controller;

import com.garritas.sgv.model.Cita;
import com.garritas.sgv.service.CitaService;
import com.garritas.sgv.service.MascotaService;
import com.garritas.sgv.service.VeterinarioService;
import com.garritas.sgv.service.ServicioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;
    private final MascotaService mascotaService;
    private final VeterinarioService veterinarioService;
    private final ServicioService servicioService;

    public CitaController(CitaService citaService, MascotaService mascotaService, VeterinarioService veterinarioService, ServicioService servicioService) {
        this.citaService = citaService;
        this.mascotaService = mascotaService;
        this.veterinarioService = veterinarioService;
        this.servicioService = servicioService;
    }

    // Vista de todas las citas, solo accesible para VETERINARIO, RECEPCIONISTA y ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA') or hasRole('ROLE_VETERINARIO')")
    @GetMapping
    public String listar(Model model) {
        List<Cita> citas = citaService.listar();
        model.addAttribute("citas", citas);
        return "citas/listar";
    }

    // Vista para ver una cita específica, solo accesible para VETERINARIO y ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VETERINARIO')")
    @GetMapping("/{id}")
    public String buscarCita(@PathVariable Long id, Model model) {
        Cita cita = citaService.buscarPorId(id).orElse(null);
        model.addAttribute("cita", cita);
        return "citas/ver";
    }

    // Vista para agregar una nueva cita, solo accesible para RECEPCIONISTA y ADMIN
    @PreAuthorize("hasRole('ROLE_RECEPCIONISTA') or hasRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarCita(Model model) {
        model.addAttribute("cita", new Cita());
        model.addAttribute("mascotas", mascotaService.listar());
        model.addAttribute("veterinarios", veterinarioService.listar());
        model.addAttribute("servicios", servicioService.listar());
        return "citas/registrar";
    }

    // Guardar una nueva cita
    @PreAuthorize("hasRole('ROLE_RECEPCIONISTA') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public String guardarCita(@ModelAttribute Cita cita) {
        citaService.guardar(cita);
        return "redirect:/citas";
    }

    // Eliminar una cita, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminarCita(@PathVariable Long id) {
        citaService.eliminar(id);
        return "redirect:/citas";
    }

    // Vista para editar una cita existente
    @PreAuthorize("hasRole('ROLE_VETERINARIO') or hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarCita(@PathVariable Long id, Model model) {
        Cita cita = citaService.buscarPorId(id).orElse(null);
        model.addAttribute("cita", cita);
        model.addAttribute("mascotas", mascotaService.listar());
        model.addAttribute("veterinarios", veterinarioService.listar());
        model.addAttribute("servicios", servicioService.listar());
        return "citas/editar";
    }

    // Guardar los cambios de una cita editada
    @PreAuthorize("hasRole('ROLE_VETERINARIO') or hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarCita(@PathVariable Long id, @ModelAttribute Cita cita) {
        cita.setIdCita(id);
        citaService.guardar(cita);
        return "redirect:/citas";
    }
}
