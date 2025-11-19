package com.garritas.sgv.controller;

import com.garritas.sgv.model.Inventario;
import com.garritas.sgv.service.InventarioService;

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
@RequestMapping("/inventarios")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    // Vista de todos los inventarios, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("listar")
    public String listar(Model model) {
        List<Inventario> inventarios = inventarioService.listar();
        model.addAttribute("inventarios", inventarios);
        return "inventarios/listar";
    }

    // Vista para ver un inventario específico, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/ver/{id}")
    public String buscarInventario(@PathVariable Long id, Model model) {
        Inventario inventario = inventarioService.buscarPorId(id).orElse(null);
        model.addAttribute("inventario", inventario);
        return "inventarios/ver";
    }

    // Vista para agregar un nuevo inventario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarInventario(Model model) {
        if (!model.containsAttribute("inventario")) {
            model.addAttribute("inventario", new Inventario());
        }
        return "inventarios/registrar";
    }

    // Guardar un nuevo inventario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/registrar")
    public String guardarInventario(@ModelAttribute("inventario") Inventario inventario, RedirectAttributes ra) {
        if (inventarioService.buscarPorNombre(inventario.getNombre()).isPresent()) {
            ra.addFlashAttribute("errorMessage", "El Nombre '" + inventario.getNombre() + "' ya está registrado.");
            return "redirect:/inventarios/registrar";
        }
        inventarioService.guardar(inventario);
        return "redirect:/inventarios/listar";
    }

    // Vista para editar un inventario existente, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarInventario(@PathVariable Long id, Model model) {
        Inventario inventario = inventarioService.buscarPorId(id).orElse(null);
        model.addAttribute("inventario", inventario);
        return "inventarios/editar";
    }

    // Guardar los cambios de un inventario editado, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarInventario(@PathVariable Long id, @ModelAttribute("inventario") Inventario inventario) {
        Inventario inventarioActualizado = inventarioService.buscarPorId(id).orElse(null);
        inventarioService.guardar(inventarioActualizado);
        return "redirect:/inventarios";
    }

    // Eliminar un inventario, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminarInventario(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return "redirect:/inventarios";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/export/excel")
    public void exportInventariosExcel(HttpServletResponse response) throws IOException {
        String filename = "inventarios_" + java.time.LocalDate.now() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<Inventario> inventarios = inventarioService.listar();

        try (var workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = workbook.createSheet("Inventarios");

            // Estilo header
            var headerStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Header
            String[] headers = {"ID", "Nombre", "Descripción", "Categoria", "Proveedor", "Precio", "Cantidad"};
            var headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Body
            int rowIdx = 1;
            for (Inventario i : inventarios) {
                var row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(safe(i.getNombre()));
                row.createCell(1).setCellValue(safe(i.getDescripcion()));
                row.createCell(2).setCellValue(safe(i.getCategoria()));
                row.createCell(3).setCellValue(safe(i.getProveedor()));
                row.createCell(4).setCellValue(i.getPrecio());
                row.createCell(4).setCellValue(i.getCantidad());;
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
