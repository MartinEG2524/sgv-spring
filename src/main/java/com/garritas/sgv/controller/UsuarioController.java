package com.garritas.sgv.controller;

import com.garritas.sgv.model.Cargo;
import com.garritas.sgv.model.Usuario;
import com.garritas.sgv.service.CargoService;
import com.garritas.sgv.service.UsuarioService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CargoService cargoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Vista de todos los usuarios, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("listar")
    public String listar(Model model) {
        List<Usuario> usuarios = usuarioService.listar();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/listar";
    }

    // Vista para ver un usuario específico, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("ver/{id}")
    public String buscarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id).orElse(null);
        model.addAttribute("usuario", usuario);
        return "usuarios/ver";
    }

    // Vista para agregar un nuevo usuario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarUsuario(Model model) {
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }
        model.addAttribute("cargos", cargoService.listar());
        return "usuarios/registrar";
    }

    // Guardar un nuevo usuario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/registrar")
    public String guardarUsuario(@ModelAttribute("usuario") Usuario usuario, RedirectAttributes ra) {
        if (usuarioService.buscarPorCodigo(usuario.getCodigo()).isPresent()) {
            ra.addFlashAttribute("errorMessage", "El código '" + usuario.getCodigo() + "' ya está registrado.");
            return "redirect:/usuarios/registrar";
        }
        usuario.getIdCargo().getIdRol();
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuario.setEstado("Activo");
        usuarioService.guardar(usuario);
        return "redirect:/usuarios/listar";
    }

    // Vista para editar un usuario existente, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id).orElse(null);
        model.addAttribute("usuario", usuario);
        model.addAttribute("cargos", cargoService.listar());
        return "usuarios/editar";
    }

    // Guardar los cambios de un usuario editado, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute("usuario") Usuario usuario, @RequestParam("idCargo.idRol") Long idRol, @RequestParam("estado") String estado) {
        Usuario usuarioActualizado = usuarioService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
        usuarioActualizado.setCodigo(usuario.getCodigo());
        if (usuario.getContrasena() != null && !usuario.getContrasena().isBlank()) {
            usuarioActualizado.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }
        Cargo cargo = cargoService.buscarPorId(idRol).orElseThrow(() -> new IllegalArgumentException("Cargo no existe: " + idRol));
        usuarioActualizado.setIdCargo(cargo);
        usuarioActualizado.setEstado(estado);
        usuarioService.actualizar(usuarioActualizado);
        return "redirect:/usuarios/listar";
    }

    // Eliminar un usuario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "redirect:/usuarios/listar";
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
            
            Usuario usuarioPerfil = usuarioService.buscarPorId(IdUsuario.longValue()).orElse(null);
            model.addAttribute("usuarios", usuarioPerfil);
            return "perfil";
        } else {
            return "redirect:/login";
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/export/excel")
    public void exportUsuariosExcel(HttpServletResponse response) throws IOException {
        String filename = "usuarios_" + java.time.LocalDate.now() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<Usuario> usuarios = usuarioService.listar();

        try (var workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = workbook.createSheet("Usuarios");

            // Estilo header
            var headerStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Header
            String[] headers = {"ID", "Código", "Estado", "Rol"};
            var headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Body
            int rowIdx = 1;
            for (Usuario u : usuarios) {
                var row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(u.getIdUsuario());
                row.createCell(1).setCellValue(safe(u.getCodigo()));
                row.createCell(2).setCellValue(safe(u.getEstado()));
                row.createCell(3).setCellValue(u.getIdCargo()!=null ? safe(u.getIdCargo().getNombre()) : "");
            }

            // Autoajuste columnas
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            workbook.write(response.getOutputStream());
        }
    }

    private String safe(String s) { 
        return s == null ? "" : s; 
    }
}
