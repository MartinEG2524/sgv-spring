package com.garritas.sgv.controller;

import com.garritas.sgv.model.Veterinario;
import com.garritas.sgv.model.Usuario;
import com.garritas.sgv.service.VeterinarioService;

import jakarta.servlet.http.HttpServletResponse;

import com.garritas.sgv.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/veterinarios")
public class VeterinarioController {

    @Autowired
    private VeterinarioService veterinarioService;

    @Autowired
    private UsuarioService usuarioService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("listar")
    public String listar(Model model) {
        List<Veterinario> veterinarios = veterinarioService.listar();
        model.addAttribute("veterinarios", veterinarios);
        return "veterinarios/listar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("ver/{id}")
    public String buscarVeterinario(@PathVariable Long id, Model model) {
        Veterinario veterinario = veterinarioService.buscarPorId(id).orElse(null);
        model.addAttribute("veterinario", veterinario);
        return "veterinarios/ver";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarVeterinario(Model model) {
        if (!model.containsAttribute("veterinario")) {
            model.addAttribute("veterinario", new Veterinario());
        }
        return "veterinarios/registrar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/registrar")
    public String guardarVeterinario(@ModelAttribute("veterinario") Veterinario veterinario, RedirectAttributes ra) {
        if (veterinarioService.buscarPorDni(veterinario.getDni()).isPresent()) {
            ra.addFlashAttribute("errorMessage", "El DNI '" + veterinario.getDni() + "' ya está registrado.");
            return "redirect:/veterinarios/registrar";
        }
        veterinario.getUsuario().getIdUsuario();  
        veterinario.setEstado("Activo");
        veterinarioService.guardar(veterinario);
        return "redirect:/veterinarios/listar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminarVeterinario(@PathVariable Long id) {
        veterinarioService.eliminar(id);
        return "redirect:/veterinarios/listar";
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarVeterinario(@PathVariable Long id, Model model) {
        Veterinario veterinario = veterinarioService.buscarPorId(id).orElse(null);
        model.addAttribute("veterinario", veterinario);
        return "veterinarios/editar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarVeterinario(@PathVariable Long id, @ModelAttribute("veterinario") Veterinario veterinario, @RequestParam("usuario.idUsuario") Long idUsuario, @RequestParam("estado") String estado) {
        Veterinario veterinarioActualizado = veterinarioService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Veterinario no encontrado: " + id));
        veterinarioActualizado.setNombres(veterinario.getNombres());
        veterinarioActualizado.setApellidos(veterinario.getApellidos());
        veterinarioActualizado.setEspecialidad(veterinario.getEspecialidad());
        veterinarioActualizado.setDni(veterinario.getDni());
        veterinarioActualizado.setCorreo(veterinario.getCorreo());
        veterinarioActualizado.setSexo(veterinario.getSexo());
        veterinarioActualizado.setFechaNacimiento(veterinario.getFechaNacimiento());
        veterinarioActualizado.setCelular(veterinario.getCelular());
        veterinarioActualizado.setPais(veterinario.getPais());
        veterinarioActualizado.setProvincia(veterinario.getProvincia());
        veterinarioActualizado.setDistrito(veterinario.getDistrito());
        veterinarioActualizado.setEstado(estado);
        Usuario usuario = usuarioService.buscarPorId(idUsuario).orElseThrow(() -> new IllegalArgumentException("Usuario no existe: " + idUsuario));
        veterinarioActualizado.setUsuario(usuario);
        veterinarioService.actualizar(veterinarioActualizado);
        return "redirect:/veterinarios/listar";
    }

    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/export/excel")
    public void exportVeterinarioExcel(HttpServletResponse response) throws IOException {
        String filename = "veterinarios_" + java.time.LocalDate.now() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<Veterinario> veterinarios = veterinarioService.listar();

        try (var workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = workbook.createSheet("Veterinarios");

            var headerStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            var createHelper = workbook.getCreationHelper();
            var dateCellStyle = workbook.createCellStyle();
            short dateFormat = createHelper.createDataFormat().getFormat("dd-MM-yyyy");
            dateCellStyle.setDataFormat(dateFormat);

            String[] headers = {"ID", "Usuario", "Nombres", "Apellidos", "DNI", "Correo", "Especialidad", "Sexo", "Fecha Nacimiento", "Celular", "Pais", "Provincia", "Distrito", "Estado"};
            var headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Veterinario v : veterinarios) {
                var row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(v.getIdVeterinario());
                row.createCell(1).setCellValue(v.getUsuario() != null ? String.valueOf(v.getUsuario().getIdUsuario()) : "");
                row.createCell(2).setCellValue(safe(v.getNombres()));
                row.createCell(3).setCellValue(safe(v.getApellidos()));
                row.createCell(4).setCellValue(v.getDni());
                row.createCell(5).setCellValue(safe(v.getCorreo()));
                row.createCell(6).setCellValue(safe(v.getEspecialidad()));
                row.createCell(7).setCellValue(safe(v.getSexo()));
                var fechaCell = row.createCell(8);
                if (v.getFechaNacimiento() != null) {
                    fechaCell.setCellValue(v.getFechaNacimiento());
                    fechaCell.setCellStyle(dateCellStyle);
                } else {
                    fechaCell.setBlank();
                }
                row.createCell(9).setCellValue(v.getCelular());
                row.createCell(10).setCellValue(safe(v.getPais()));
                row.createCell(11).setCellValue(safe(v.getProvincia()));
                row.createCell(12).setCellValue(safe(v.getDistrito()));
                row.createCell(13).setCellValue(safe(v.getEstado()));
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            workbook.write(response.getOutputStream());
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
