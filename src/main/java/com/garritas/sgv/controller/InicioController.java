package com.garritas.sgv.controller;

import com.garritas.sgv.model.Cargo;
import com.garritas.sgv.model.Usuario;
import com.garritas.sgv.service.UsuarioService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class InicioController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping({"/", "/inicio"})
    public String Inicio(Authentication authentication) {
        // Si está autenticado y no es anónimo, redirigir a menu
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/menu";
        }
        // Si NO está logueado, mostrar la página de inicio normal
        return "inicio";
    }

    @GetMapping("/login")
    public String Login(String error, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/menu";
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Usuario o Contraseña Incorrecta.");
        }
        return "login";
    }

    @GetMapping("/logout")
    public String Logout(String logout, Model model, HttpServletRequest request) {
        if (logout != null) {
            model.addAttribute("logoutMessage", "¡Sesión cerrada correctamente!");
        }
        request.getSession().invalidate();
        return "login";
    }

    @GetMapping("/acceso-denegado")
    public String AccesoDenegado() {
        return "acceso-denegado";
    }

    @GetMapping("/registrar")
    public String Registrar() {
        return "registrar";
    }

    @PostMapping("/registrar")
    public String RegistrarUsuarioCliente(@RequestParam String codigo, @RequestParam String contrasena, Model model) {
        if (usuarioService.buscarPorCodigo(codigo).isPresent()) {
            model.addAttribute("errorMessage", "El código ya está registrado.");
            return "registrar";
        }
        Cargo cargo = new Cargo();
        cargo.setIdRol(4L);
        Usuario usuario = new Usuario();
        usuario.setCodigo(codigo);
        usuario.setContrasena(passwordEncoder.encode(contrasena));
        usuario.setIdCargo(cargo);
        usuarioService.guardar(usuario);
        model.addAttribute("successMessage", "Cuenta '" + usuario.getCodigo() + "' creado exitosamente, ya puede iniciar sesión.");
        return "registrar";
    }

    @GetMapping("/restablecer")
    public String RestablecerContraseña() {
        return "restablecer";
    }

    @PostMapping("/restablecer")
    public String resetPassword(@RequestParam("codigo") String codigo, @RequestParam("nuevaContrasena") String nuevaContrasena, Model model) {
        boolean isUpdated = usuarioService.actualizarContrasena(codigo, nuevaContrasena);

        if (isUpdated) {
            model.addAttribute("successMessage", "Contraseña actualizada correctamente.");
        } else {
            model.addAttribute("errorMessage", "El código de usuario no es válido.");
        }

        return "restablecer";
    }

    @GetMapping("/menu")
    public String Menu(Model model) {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated() && !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User username = (User) authentication.getPrincipal();
            String codigo = username.getUsername();
            String rol = username.getAuthorities().toArray()[0].toString();
            model.addAttribute("codigo", codigo);
            model.addAttribute("rol", rol);

            Optional<Usuario> usuarioOptional = usuarioService.buscarPorCodigo(codigo);

            Usuario usuario = usuarioOptional.get();

            Cargo cargo = usuario.getIdCargo();

            Long IdRol = cargo.getIdRol();
            Long IdUsuario = usuario.getIdUsuario();

            model.addAttribute("IdRol", IdRol);
            model.addAttribute("IdUsuario", IdUsuario);

            return "menu";
        }
        return "login";
    }

    @GetMapping("/error")
    public String Error() {
        return "error";
    }

    @GetMapping("/contacto")
    public String Contacto() {
        return "contacto";
    }

    @GetMapping("/servicios-info")
    public String Servicios() {
        return "servicios-info";
    }

    @GetMapping("/nosotros")
    public String Nosotros() {
        return "nosotros";
    }
}
