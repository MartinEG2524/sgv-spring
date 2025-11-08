package com.garritas.sgv.controller;

import com.garritas.sgv.model.Servicio;
import com.garritas.sgv.service.ServicioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/servicios")
public class ServicioController {

    private final ServicioService service;

    public ServicioController(ServicioService service) {
        this.service = service;
    }

    // Vista de todos los servicios, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping
    public String listar(Model model) {
        List<Servicio> servicios = service.listar();
        model.addAttribute("servicios", servicios);
        return "servicios/listar";
    }

    // Vista para ver un servicio específico, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/{id}")
    public String buscarServicio(@PathVariable Long id, Model model) {
        Servicio servicio = service.buscarPorId(id).orElse(null);
        model.addAttribute("servicio", servicio);
        return "servicios/ver";
    }

    // Vista para agregar un nuevo servicio, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarServicio(Model model) {
        model.addAttribute("servicio", new Servicio());
        return "servicios/registrar";
    }

    // Guardar un nuevo servicio, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public String guardarServicio(@ModelAttribute Servicio servicio) {
        service.guardar(servicio);
        return "redirect:/servicios";
    }

    // Eliminar un servicio, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminarServicio(@PathVariable Long id) {
        service.eliminar(id);
        return "redirect:/servicios";
    }

    // Vista para editar un servicio existente, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarServicio(@PathVariable Long id, Model model) {
        Servicio servicio = service.buscarPorId(id).orElse(null);
        model.addAttribute("servicio", servicio);
        return "servicios/editar";
    }

    // Guardar los cambios de un servicio editado, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarServicio(@PathVariable Long id, @ModelAttribute Servicio servicio) {
        servicio.setIdServicio(id);
        service.guardar(servicio);
        return "redirect:/servicios";
    }
}
