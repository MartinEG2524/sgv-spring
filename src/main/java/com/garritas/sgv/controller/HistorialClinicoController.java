package com.garritas.sgv.controller;

import com.garritas.sgv.model.HistorialClinico;
import com.garritas.sgv.model.Mascota;
import com.garritas.sgv.model.Veterinario;
import com.garritas.sgv.service.HistorialClinicoService;
import com.garritas.sgv.service.MascotaService;
import com.garritas.sgv.service.VeterinarioService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/historiales")
public class HistorialClinicoController {

    @Autowired
    private HistorialClinicoService historialClinicoService;

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private VeterinarioService veterinarioService;

    public HistorialClinicoController(HistorialClinicoService historialClinicoService) {
        this.historialClinicoService = historialClinicoService;
    }

    // Vista de todos los historiales clínicos, solo accesible para ADMIN y VETERINARIO
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VETERINARIO')")
    @GetMapping("/listar")
    public String listar(Model model) {
        List<HistorialClinico> historiales = historialClinicoService.listar();
        model.addAttribute("historiales", historiales);
        return "historiales/listar";
    }

    // Vista para ver un historial clínico específico, solo accesible para ADMIN y VETERINARIO
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VETERINARIO')")
    @GetMapping("/ver/{id}")
    public String buscarHistorial(@PathVariable Long id, Model model) {
        HistorialClinico historial = historialClinicoService.buscarPorId(id).orElse(null);
        model.addAttribute("historial", historial);
        return "historiales/ver";
    }

    // Vista para agregar un nuevo historial clínico, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/registrar")
    public String registrarHistorial(Model model) {
        model.addAttribute("historial", new HistorialClinico());
        return "historiales/registrar";
    }

    // Guardar un nuevo historial clínico, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @PostMapping("/registrar")
    public String guardarHistorial(@ModelAttribute("historial") HistorialClinico historial, RedirectAttributes ra) {
        if (historialClinicoService.buscarPorCodigo(historial.getCodigo()).isPresent()) {
            ra.addFlashAttribute("errorMessage", "El historial Clinico '" + historial.getCodigo() + "' ya está registrado.");
            return "redirect:/historiales/registrar";
        }
        historial.getMascota().getIdMascota();
        historial.getVeterinario().getIdVeterinario();
        historial.setEstado("Activo");
        historialClinicoService.guardar(historial);
        return "redirect:/historiales/listar";
    }

    // Eliminar un historial clínico, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminarHistorial(@PathVariable Long id) {
        historialClinicoService.eliminar(id);
        return "redirect:/historiales/listar";
    }

    // Vista para editar un historial clínico existente, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/editar/{id}")
    public String editarHistorial(@PathVariable Long id, Model model) {
        HistorialClinico historial = historialClinicoService.buscarPorId(id).orElse(null);
        model.addAttribute("historial", historial);
        model.addAttribute("mascotas", mascotaService.listar());
        model.addAttribute("veterinarios", veterinarioService.listar());
        return "historiales/editar";
    }

    // Guardar los cambios de un historial clínico editado, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarHistorial(@PathVariable Long id, @ModelAttribute("historial") HistorialClinico historial, @RequestParam("mascota.idMascota") Long idMascota, @RequestParam("veterinario.idVeterinario") Long idVeterinario, @RequestParam("estado") String estado) {
        HistorialClinico historialActualizado = historialClinicoService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Historial Clinico no encontrado: " + id));
        historialActualizado.setCodigo(historial.getCodigo());
        historialActualizado.setEstado(historial.getEstado());
        historialActualizado.setDiagnostico(historial.getDiagnostico());
        historialActualizado.setFecha(historial.getFecha());
        historialActualizado.setIdHistorial(historial.getIdHistorial());
        historialActualizado.setMascota(historial.getMascota());
        historialActualizado.setTratamiento(historial.getTratamiento());
        historialActualizado.setVeterinario(historial.getVeterinario());
        Mascota mascota = mascotaService.buscarPorId(idMascota).orElseThrow(() -> new IllegalArgumentException("Mascota no existe: " + idMascota));
        historialActualizado.setMascota(mascota);
        Veterinario veterinario = veterinarioService.buscarPorId(idMascota).orElseThrow(() -> new IllegalArgumentException("Veterinario no existe: " + idVeterinario));
        historialActualizado.setVeterinario(veterinario);
        historialActualizado.setEstado(estado);
        historialClinicoService.actualizar(historial);
        return "redirect:/historiales/listar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/export/excel")
    public void exportHistorialesExcel(HttpServletResponse response) throws IOException {
        String filename = "historial clinico_" + java.time.LocalDate.now() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<HistorialClinico> historialClinico = historialClinicoService.listar();

        try (var workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = workbook.createSheet("Historales");

            // Estilo header
            var headerStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // ==== Estilo fecha ====
            var createHelper = workbook.getCreationHelper();
            var dateCellStyle = workbook.createCellStyle();
            short dateFormat = createHelper.createDataFormat().getFormat("dd-MM-yyyy");
            dateCellStyle.setDataFormat(dateFormat);

            // Header
            String[] headers = {"ID", "Mascota", "Veterinario", "Codigo", "Fecha", "Diagnostico", "Tratamiento", "Estado"};
            var headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Body
            int rowIdx = 1;
            for (HistorialClinico c : historialClinico) {
                var row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.getIdHistorial());
                row.createCell(1).setCellValue(c.getMascota()!=null ? String.valueOf(c.getMascota().getIdMascota()) : "");
                row.createCell(2).setCellValue(c.getVeterinario()!=null ? String.valueOf(c.getVeterinario().getIdVeterinario()) : "");
                row.createCell(3).setCellValue(safe(c.getCodigo()));
                var fechaCell = row.createCell(4);
                if (c.getFecha() != null) {
                    fechaCell.setCellValue(c.getFecha());
                    fechaCell.setCellStyle(dateCellStyle);
                } else {
                    fechaCell.setBlank();
                }
                row.createCell(5).setCellValue(safe(c.getDiagnostico()));
                row.createCell(6).setCellValue(safe(c.getTratamiento()));
                row.createCell(7).setCellValue(safe(c.getEstado()));
                
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
