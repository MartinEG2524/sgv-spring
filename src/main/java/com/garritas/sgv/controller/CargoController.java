package com.garritas.sgv.controller;

import com.garritas.sgv.model.Cargo;
import com.garritas.sgv.service.CargoService;

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
@RequestMapping("/cargos")
public class CargoController {

    @Autowired
    private CargoService cargoService;

    // Vista de listado de cargos, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Cargo> cargos = cargoService.listar();
        model.addAttribute("cargos", cargos);
        return "cargos/listar";
    }

    // Vista para ver un cargo específico, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("ver/{id}")
    public String buscarCargo(@PathVariable Long id, Model model) {
        Cargo cargo = cargoService.buscarPorId(id).orElse(null);
        model.addAttribute("cargo", cargo);
        return "cargos/ver";
    }

    // Vista para agregar un nuevo cargo, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarCargo(Model model) {
        if (!model.containsAttribute("cargo")) {
            model.addAttribute("cargo", new Cargo());
        }
        return "cargos/registrar";
    }

    // Guardar un nuevo cargo, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/registrar")
    public String guardarCargo(@ModelAttribute("cargo") Cargo cargo, RedirectAttributes ra) {
        if (cargoService.buscarPorNombre(cargo.getNombre()).isPresent()) {
            ra.addFlashAttribute("errorMessage", "El Cargo '" + cargo.getNombre() + "' ya está registrado.");
            return "redirect:/cargos/registrar";
        }
        cargo.setEstado("Activo");
        cargoService.guardar(cargo);
        return "redirect:/cargos/listar";
    }

    // Vista para editar un cargo, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarCargo(@PathVariable Long id, Model model) {
        Cargo cargo = cargoService.buscarPorId(id).orElse(null);
        model.addAttribute("cargo", cargo);
        return "cargos/editar";
    }

    // Guardar los cambios de un cargo editado, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarCargo(@PathVariable Long id, @ModelAttribute("cargo") Cargo cargo, @RequestParam("estado") String estado) {
        Cargo cargoActualizado = cargoService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + id));
        cargoActualizado.setNombre(cargo.getNombre());
        cargoActualizado.setDescripcion(cargo.getDescripcion());
        cargoActualizado.setEstado(estado);
        cargoService.actualizar(cargoActualizado);
        return "redirect:/cargos/listar";
    }

    // Eliminar un cargo, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminarCargo(@PathVariable Long id) {
        cargoService.eliminar(id);
        return "redirect:/cargos/listar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/export/excel")
    public void exportCargosExcel(HttpServletResponse response) throws IOException {
        String filename = "cargos_" + java.time.LocalDate.now() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<Cargo> cargos = cargoService.listar();

        try (var workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = workbook.createSheet("Cargos");

            // Estilo header
            var headerStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Header
            String[] headers = {"ID", "Nombre", "Descripción", "Estado"};
            var headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Body
            int rowIdx = 1;
            for (Cargo c : cargos) {
                var row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.getIdRol());
                row.createCell(1).setCellValue(safe(c.getNombre()));
                row.createCell(2).setCellValue(safe(c.getDescripcion()));
                row.createCell(3).setCellValue(safe(c.getEstado()));
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
