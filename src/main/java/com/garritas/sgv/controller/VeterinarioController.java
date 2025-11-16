package com.garritas.sgv.controller;

import com.garritas.sgv.model.Veterinario;
import com.garritas.sgv.model.Usuario;
import com.garritas.sgv.service.VeterinarioService;
import com.garritas.sgv.service.UsuarioService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;
    private final UsuarioService usuarioService;

    public VeterinarioController(VeterinarioService veterinarioService, UsuarioService usuarioService) {
        this.veterinarioService = veterinarioService;
        this.usuarioService = usuarioService;
    }

    // LISTAR VETERINARIOS
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Veterinario> veterinarios = veterinarioService.listar();
        model.addAttribute("veterinarios", veterinarios);
        return "veterinarios/listar";
    }

    // VER PERFIL DE UN VETERINARIO
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/ver/{id}")
    public String verVeterinario(@PathVariable Long id, Model model) {
        Veterinario veterinario = veterinarioService.buscarPorId(id).orElse(null);
        model.addAttribute("veterinario", veterinario);
        return "veterinarios/ver";
    }

    // REGISTRAR VETERINARIO
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrar(Model model) {
        if (!model.containsAttribute("veterinario")) {
            model.addAttribute("veterinario", new Veterinario());
        }
        return "veterinarios/registrar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/registrar")
    public String guardar(@ModelAttribute Veterinario veterinario, RedirectAttributes ra) {

        veterinario.setEstado("activo");

        veterinarioService.guardar(veterinario);
        return "redirect:/veterinarios/listar";
    }

    // EDITAR VETERINARIO
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        Veterinario veterinario = veterinarioService.buscarPorId(id).orElse(null);
        model.addAttribute("veterinario", veterinario);

        return "veterinarios/editar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute Veterinario veterinario,
                             @RequestParam("usuario.idUsuario") Long idUsuario) {

        Veterinario vetActual = veterinarioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Veterinario no encontrado: " + id));

        // ACTUALIZAR CAMPOS
        vetActual.setNombres(veterinario.getNombres());
        vetActual.setApellidos(veterinario.getApellidos());
        vetActual.setDni(veterinario.getDni());
        vetActual.setCorreo(veterinario.getCorreo());
        vetActual.setEspecialidad(veterinario.getEspecialidad());
        vetActual.setSexo(veterinario.getSexo());
        vetActual.setFechaNacimiento(veterinario.getFechaNacimiento());
        vetActual.setCelular(veterinario.getCelular());
        vetActual.setPais(veterinario.getPais());
        vetActual.setProvincia(veterinario.getProvincia());
        vetActual.setDistrito(veterinario.getDistrito());
        vetActual.setEstado(veterinario.getEstado());

        Usuario usuario = usuarioService.buscarPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe: " + idUsuario));

        vetActual.setUsuario(usuario);

        veterinarioService.guardar(vetActual);

        return "redirect:/veterinarios/listar";
    }

    // ELIMINAR VETERINARIO
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        veterinarioService.eliminar(id);
        return "redirect:/veterinarios/listar";
    }
}
