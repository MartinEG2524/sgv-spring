package com.garritas.sgv.controller;

import com.garritas.sgv.model.Usuario;
import com.garritas.sgv.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Vista de todos los usuarios, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public String listar(Model model) {
        List<Usuario> usuarios = usuarioService.listar();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/listar";
    }

    // Vista para ver un usuario específico, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public String buscarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id).orElse(null);
        model.addAttribute("usuario", usuario);
        return "usuarios/ver";
    }

    // Vista para agregar un nuevo usuario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/registrar";
    }

    // Guardar un nuevo usuario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        usuarioService.guardar(usuario);
        return "redirect:/usuarios";
    }

    // Eliminar un usuario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "redirect:/usuarios";
    }

    // Vista para editar un usuario existente, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id).orElse(null);
        model.addAttribute("usuario", usuario);
        return "usuarios/editar";
    }

    // Guardar los cambios de un usuario editado, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute Usuario usuario) {
        usuario.setIdUsuario(id);
        usuarioService.guardar(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/perfil")
    public String perfilUsuario(@RequestParam("IdRol") Integer IdRol, @RequestParam("IdUsuario") Integer IdUsuario, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            User username = (User) authentication.getPrincipal();
            String rol = username.getAuthorities().toArray()[0].toString();
            model.addAttribute("rol", rol);

            model.addAttribute("IdRol", IdRol);
            model.addAttribute("IdUsuario", IdUsuario);

            List<Usuario> usuario = usuarioService.buscarUsuario(IdRol, IdUsuario);
            model.addAttribute("usuarios", usuario);

            return "perfil";

        } else {
            return "redirect:/login";
        }
    }
}
