package com.garritas.sgv.controller;

import com.garritas.sgv.model.DetalleHistorialInventario;
import com.garritas.sgv.model.HistorialClinico;
import com.garritas.sgv.model.Inventario;
import com.garritas.sgv.service.DetalleHistorialInventarioService;
import com.garritas.sgv.service.HistorialClinicoService;
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
@RequestMapping("/detalles")
public class DetalleHistorialInventarioController {

    @Autowired
    private DetalleHistorialInventarioService detalleHistorialInventarioService;

    @Autowired
    private HistorialClinicoService historialClinicoService;

    @Autowired
    private InventarioService inventarioService;

    public DetalleHistorialInventarioController(DetalleHistorialInventarioService detalleHistorialInventarioService) {
        this.detalleHistorialInventarioService = detalleHistorialInventarioService;
    }

    // Vista de todos los detalles del historial de inventario, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') ")
    @GetMapping("listar")
    public String listar(Model model) {
        List<DetalleHistorialInventario> detalleHistorialInventarios = detalleHistorialInventarioService.listar();
        model.addAttribute("detalles", detalleHistorialInventarios);
        return "detalles/listar";
    }

    // Vista para ver un detalle específico, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/ver/{id}")
    public String buscarDetalle(@PathVariable Long id, Model model) {
        DetalleHistorialInventario detalleHistorialInventarios = detalleHistorialInventarioService.buscarPorId(id).orElse(null);
        model.addAttribute("detalle", detalleHistorialInventarios);
        return "detalles/ver";
    }

    // Vista para agregar un nuevo detalle
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registar")
    public String registarDetalle(Model model) {
        model.addAttribute("detalle", new DetalleHistorialInventario());
        return "detalles/registar";
    }

    // Guardar un nuevo detalle
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/registrar")
    public String guardarDetalle(@ModelAttribute("detalle") DetalleHistorialInventario detalle, RedirectAttributes ra) {
        if (detalleHistorialInventarioService.buscarPorIdHistorial(detalle.getHistorial().getIdHistorial()).isPresent()) {
            ra.addFlashAttribute("errorMessage", "El Historial '" + detalle.getHistorial().getIdHistorial() + "' no existe.");
            return "redirect:/detalles/registrar";
        }
        if (detalleHistorialInventarioService.buscarPorIdProducto(detalle.getProducto().getIdProducto()).isPresent()) {
            ra.addFlashAttribute("errorMessage", "El Producto '" + detalle.getProducto().getIdProducto() + "' no existe.");
            return "redirect:/detalles/registrar";
        }
        detalle.getHistorial().getIdHistorial();
        detalle.getProducto().getIdProducto();
        detalleHistorialInventarioService.guardar(detalle);
        return "redirect:/detalles/listar";
    }

    // Vista para editar un detalle existente, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarDetalle(@PathVariable Long id, Model model) {
        DetalleHistorialInventario detalleHistorialInventario = detalleHistorialInventarioService.buscarPorId(id).orElse(null);
        model.addAttribute("detalle", detalleHistorialInventario);
        model.addAttribute("historiales", historialClinicoService.listar());
        model.addAttribute("inventarios", inventarioService.listar());
        return "detalles/editar";
    }

    // Guardar los cambios de un detalle editado, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarDetalle(@PathVariable Long id, @ModelAttribute("detalle") DetalleHistorialInventario detalle, @RequestParam("historial.idHistorial") Long idHistorial, @RequestParam("inventario.idProducto") Long idProducto) {
        DetalleHistorialInventario detalleActualizado = detalleHistorialInventarioService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado: " + id));
        detalleActualizado.setCantidadUtilizada(detalle.getCantidadUtilizada());
        HistorialClinico historialClinico = historialClinicoService.buscarPorId(idHistorial).orElseThrow(() -> new IllegalArgumentException("Historial no existe: " + idHistorial));
        detalleActualizado.setHistorial(historialClinico);
        Inventario inventario = inventarioService.buscarPorId(idProducto).orElseThrow(() -> new IllegalArgumentException("Producto no existe: " + idProducto));
        detalleActualizado.setProducto(inventario);
        detalleHistorialInventarioService.actualizar(detalle);
        return "redirect:/detalles";
    }

    // Eliminar un detalle, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminarDetalle(@PathVariable Long id) {
        detalleHistorialInventarioService.eliminar(id);
        return "redirect:/detalles/listar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/export/excel")
    public void exportDetallesExcel(HttpServletResponse response) throws IOException {
        String filename = "detalle historial inventario_" + java.time.LocalDate.now() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<DetalleHistorialInventario> detalleHistorialInventarios = detalleHistorialInventarioService.listar();

        try (var workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = workbook.createSheet("Detalles");

            // Estilo header
            var headerStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Header
            String[] headers = {"ID", "Cantidad Utilizada", "Historial", "Producto"};
            var headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Body
            int rowIdx = 1;
            for (DetalleHistorialInventario d : detalleHistorialInventarios) {
                var row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(d.getIdDetalle());
                row.createCell(1).setCellValue(d.getCantidadUtilizada());
                row.createCell(2).setCellValue(d.getHistorial() != null ? String.valueOf(d.getHistorial().getIdHistorial()) : "");
                row.createCell(2).setCellValue(d.getProducto() != null ? String.valueOf(d.getProducto().getIdProducto()) : "");
            }

            // Autoajuste columnas
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            workbook.write(response.getOutputStream());
        }
    }
}
