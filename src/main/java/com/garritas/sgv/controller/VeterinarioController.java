package com.garritas.sgv.controller;

import com.garritas.sgv.model.Veterinario;
import com.garritas.sgv.service.VeterinarioService;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    // Vista de listado de veterinarios, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public String listar(Model model) {
        List<Veterinario> veterinarios = veterinarioService.listar();
        model.addAttribute("veterinarios", veterinarios);
        return "listar";
    }

    // Vista de perfil del veterinario, solo accesible para veterinarios o administradores
    @PreAuthorize("hasRole('ROLE_VETERINARIO') or hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public String buscarVeterinario(@PathVariable Long id, Model model) {
        Veterinario veterinario = veterinarioService.buscarPorId(id).orElse(null);
        model.addAttribute("veterinario", veterinario);
        return "veterinarios/perfil";
    }
    
    // Vista de gestión de citas, solo accesible para veterinarios o administradores
    @PreAuthorize("hasRole('ROLE_VETERINARIO') or hasRole('ROLE_ADMIN')")
    @GetMapping("/citas")
    public String vistaGestionCitas(Model model) {
        model.addAttribute("citas", veterinarioService.obtenerCitas());
        return "veterinarios/citas";
    }

    // Vista de historial clínico de mascotas, solo accesible para veterinarios o administradores
    @PreAuthorize("hasRole('ROLE_VETERINARIO') or hasRole('ROLE_ADMIN')")
    @GetMapping("/historial")
    public String vistaHistorialClinico(Model model) {
        model.addAttribute("historial", veterinarioService.obtenerHistorial());
        return "veterinarios/historial";
    }

    // Vista de gestión de servicios veterinarios, solo accesible para veterinarios, recepcionistas o administradores
    @PreAuthorize("hasRole('ROLE_VETERINARIO') or hasRole('ROLE_RECEPCIONISTA') or hasRole('ROLE_ADMIN')")
    @GetMapping("/servicios")
    public String vistaGestionServicios(Model model) {
        model.addAttribute("servicios", veterinarioService.obtenerServicios());
        return "veterinarios/servicios";
    }

    // Vista para agregar un nuevo veterinario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarVeterinario(Model model) {
        model.addAttribute("veterinario", new Veterinario());
        return "veterinarios/registrar";
    }

    // Guardar un nuevo veterinario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public String guardarVeterinario(@ModelAttribute Veterinario veterinario) {
        veterinarioService.guardar(veterinario);
        return "redirect:/veterinarios";
    }

    // Vista para editar un veterinario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarVeterinario(@PathVariable Long id, Model model) {
        Veterinario veterinario = veterinarioService.buscarPorId(id).orElse(null);
        model.addAttribute("veterinario", veterinario);
        return "veterinarios/editar";
    }

    // Guardar los cambios de un veterinario editado, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarVeterinario(@PathVariable Long id, @ModelAttribute Veterinario veterinario) {
        veterinario.setIdVeterinario(id);
        veterinarioService.guardar(veterinario);
        return "redirect:/veterinarios";
    }

    // Eliminar un veterinario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminarVeterinario(@PathVariable Long id) {
        veterinarioService.eliminar(id);
        return "redirect:/veterinarios";
    }
}
