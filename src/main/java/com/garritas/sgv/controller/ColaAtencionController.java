package com.garritas.sgv.controller;

import com.garritas.sgv.model.ColaAtencion;
import com.garritas.sgv.model.Mascota;
import com.garritas.sgv.service.ColaAtencionService;
import com.garritas.sgv.service.MascotaService;

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
@RequestMapping("/colas")
public class ColaAtencionController {

    @Autowired
    private ColaAtencionService colaService;

    @Autowired
    private MascotaService mascotaService;

    // Vista de todas las atenciones - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("listar")
    public String listar(Model model) {
        List<ColaAtencion> cola = colaService.listar();
        model.addAttribute("colas", cola);
        return "colas/listar";
    }

    // Listado de pendientes (No atendidos) - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("pendientes")
    public String listarPendientes(Model model) {
        List<ColaAtencion> cola = colaService.listarPendientes();
        model.addAttribute("colas", cola);
        return "colas/pendientes";
    }

    // Ver un registro de cola - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("ver/{id}")
    public String buscarCola(@PathVariable Long id, Model model) {
        ColaAtencion cola = colaService.buscarPorId(id).orElse(null);
        model.addAttribute("cola", cola);
        return "colas/ver";
    }

    // Registrar ingreso de cola - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/registrar")
    public String registrarCola(Model model) {
        if (!model.containsAttribute("cola")) {
            model.addAttribute("cola", new ColaAtencion());
        }
        model.addAttribute("mascotas", mascotaService.listar());
        return "colas/registrar";
    }

    // Guardar nuevo ingreso - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @PostMapping("/registrar")
    public String guardarCola(@ModelAttribute("cola") ColaAtencion cola, RedirectAttributes ra) {
        if (colaService.buscarPorFechaIngreso(cola.getFechaIngreso()).isPresent()) {
            ra.addFlashAttribute("errorMessage", "La Fecha Ingreso '" + cola.getFechaIngreso() + "' ya está registrado.");
            return "redirect:/colas/registrar";
        }
        cola.getMascota().getIdMascota();
        cola.setAtendido(false);
        cola.setEstado("Activo");
        colaService.guardar(cola);
        return "redirect:/colas/listar";
    }

    // Editar un registro existente - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/editar/{id}")
    public String editarCola(@PathVariable Long id, Model model) {
        ColaAtencion cola = colaService.buscarPorId(id).orElse(null);
        model.addAttribute("cola", cola);
        model.addAttribute("mascotas", mascotaService.listar());
        return "colas/editar";
    }

    // Actualizar registro - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @PostMapping("/editar/{id}")
    public String actualizarCola(@PathVariable Long id, @ModelAttribute("cola") ColaAtencion cola, @RequestParam("mascota.idMascota") Long idMascota, @RequestParam("atendido") String atendido, @RequestParam("estado") String estado) {
        ColaAtencion colaActualizada = colaService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Cola Atención no encontrado: " + id));
        colaActualizada.setFechaIngreso(cola.getFechaIngreso());
        colaActualizada.setAtendido(cola.getAtendido());
        colaActualizada.setEstado(cola.getEstado());
        Mascota mascota = mascotaService.buscarPorId(idMascota).orElseThrow(() -> new IllegalArgumentException("Mascota no existe: " + idMascota));
        colaActualizada.setMascota(mascota);
        colaService.guardar(colaActualizada);
        return "redirect:/colas/listar";
    }

    // Eliminar un registro - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @PostMapping("/eliminar/{id}")
    public String eliminarCola(@PathVariable Long id) {
        colaService.eliminar(id);
        return "redirect:/colas/listar";
    }

    // Marcar como atendido - solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @PostMapping("/marcar-atendido/{id}")
    public String marcarAtendidoCola(@PathVariable Long id) {
        colaService.marcarAtendido(id);
        return "redirect:/colas/pendientes";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/export/excel")
    public void exportColasExcel(HttpServletResponse response) throws IOException {
        String filename = "Cola Atención_" + java.time.LocalDate.now() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<ColaAtencion> colas = colaService.listar();

        try (var workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = workbook.createSheet("Colas");

            // Estilo header
            var headerStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // ==== Estilo fecha/hora ====
            var createHelper = workbook.getCreationHelper();
            var dateCellStyle = workbook.createCellStyle();
            short dateFormat = createHelper.createDataFormat().getFormat("dd-MM-yyyy HH:mm:ss");
            dateCellStyle.setDataFormat(dateFormat);

            // Header
            String[] headers = {"ID", "Mascota", "Fecha Ingreso", "Atendido", "Estado"};
            var headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Body
            int rowIdx = 1;
            for (ColaAtencion c : colas) {
                var row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.getIdCola());
                row.createCell(1).setCellValue(c.getMascota() != null ? String.valueOf(c.getMascota().getIdMascota()) : "");
                var fechaCell = row.createCell(2);
                if (c.getFechaIngreso() != null) {
                    fechaCell.setCellValue(c.getFechaIngreso());
                    fechaCell.setCellStyle(dateCellStyle);
                } else {
                    fechaCell.setBlank();
                }
                var atendidoCell = row.createCell(3);
                atendidoCell.setCellValue(c.getAtendido() ? "Sí" : "No");
                row.createCell(4).setCellValue(safe(c.getEstado()));
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
