package com.garritas.sgv.controller;

import com.garritas.sgv.model.Servicio;
import com.garritas.sgv.service.ServicioService;

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
@RequestMapping("/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    // Vista de todos los servicios, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("listar")
    public String listar(Model model) {
        List<Servicio> servicios = servicioService.listar();
        model.addAttribute("servicios", servicios);
        return "servicios/listar";
    }

    // Vista para ver un servicio específico, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("ver/{id}")
    public String buscarServicio(@PathVariable Long id, Model model) {
        Servicio servicio = servicioService.buscarPorId(id).orElse(null);
        model.addAttribute("servicio", servicio);
        return "servicios/ver";
    }

    // Vista para agregar un nuevo servicio, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarServicio(Model model) {
        if (!model.containsAttribute("servicio")) {
            model.addAttribute("servicio", new Servicio());
        }
        return "servicios/registrar";
    }

    // Guardar un nuevo servicio, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public String guardarServicio(@ModelAttribute("servicio") Servicio servicio, RedirectAttributes ra) {
        if (servicioService.buscarPorDescripcion(servicio.getDescripcion()).isPresent()) {
            ra.addFlashAttribute("errorMessage", "La Descripción '" + servicio.getDescripcion() + "' ya está registrado.");
            return "redirect:/servicios/registrar";
        }
        servicioService.guardar(servicio);
        return "redirect:/servicios/listar";
    }

    // Vista para editar un servicio existente, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarServicio(@PathVariable Long id, Model model) {
        Servicio servicio = servicioService.buscarPorId(id).orElse(null);
        model.addAttribute("servicio", servicio);
        return "servicios/editar";
    }

    // Guardar los cambios de un servicio editado, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarServicio(@PathVariable Long id, @ModelAttribute("servicio") Servicio servicio, @RequestParam("estado") String estado) {
        Servicio servicioActualizado = servicioService.buscarPorId(id).orElse(null);
        servicioActualizado.setTipo(servicio.getTipo());
        servicioActualizado.setDescripcion(servicio.getDescripcion());
        servicioActualizado.setPrecio(servicio.getPrecio());
        servicioActualizado.setEstado(estado);
        servicioService.guardar(servicioActualizado);
        return "redirect:/servicios/listar";
    }

    // Eliminar un servicio, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminarServicio(@PathVariable Long id) {
        servicioService.eliminar(id);
        return "redirect:/servicios/listar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/export/excel")
    public void exportInventariosExcel(HttpServletResponse response) throws IOException {
        String filename = "servicios_" + java.time.LocalDate.now() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<Servicio> servicios = servicioService.listar();

        try (var workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = workbook.createSheet("Servicios");

            // Estilo header
            var headerStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Header
            String[] headers = {"ID", "Tipo", "Descripción", "Precio", "Estado"};
            var headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Body
            int rowIdx = 1;
            for (Servicio s : servicios) {
                var row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(s.getIdServicio());
                row.createCell(1).setCellValue(safe(s.getTipo()));
                row.createCell(2).setCellValue(safe(s.getDescripcion()));
                row.createCell(3).setCellValue(s.getPrecio());
                row.createCell(4).setCellValue(safe(s.getEstado()));
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
