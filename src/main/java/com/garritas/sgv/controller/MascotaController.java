package com.garritas.sgv.controller;

import com.garritas.sgv.model.Cliente;
import com.garritas.sgv.model.Mascota;
import com.garritas.sgv.service.ClienteService;
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
@RequestMapping("/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private ClienteService clienteService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA') or hasRole('ROLE_CLIENTE') or hasRole('ROLE_VETERINARIO')")
    @GetMapping("/inicio")
    public String inicioMascota() {
        return "mascotas/inicio";
    }

    // Vista de todos las mascotas, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("listar")
    public String listar(Model model) {
        List<Mascota> mascotas = mascotaService.listar();
        model.addAttribute("mascotas", mascotas);
        return "mascotas/listar";
    }

    // Vista para ver una mascota específica, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("ver/{id}")
    public String buscarMascota(@PathVariable Long id, Model model) {
        Mascota mascota = mascotaService.buscarPorId(id).orElse(null);
        model.addAttribute("mascota", mascota);
        return "mascotas/ver";
    }

    // Vista para agregar una nueva mascota, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarMascota(Model model) {
        if (!model.containsAttribute("mascota")) {
            model.addAttribute("mascota", new Mascota());
        }
        return "mascotas/registrar";
    }

    // Guardar una nueva mascota, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/registrar")
    public String guardarMascota(@ModelAttribute("mascota") Mascota mascota, RedirectAttributes ra) {
        if (mascotaService.buscarPorDni(mascota.getDni()).isPresent()) {
            ra.addFlashAttribute("errorMessage", "El DNI '" + mascota.getDni() + "' ya está registrado.");
            return "redirect:/mascotas/registrar";
        }

        if (mascotaService.buscarPorCodigo(mascota.getCodigo()).isPresent()) {
            ra.addFlashAttribute("errorMessage", "El Código '" + mascota.getCodigo() + "' ya está registrado.");
            return "redirect:/mascotas/registrar";
        }
        mascotaService.guardar(mascota);
        return "redirect:/mascotas/listar";
    }

    // Vista para editar una mascota existente, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarMascota(@PathVariable Long id, Model model) {
        Mascota mascota = mascotaService.buscarPorId(id).orElse(null);
        model.addAttribute("mascota", mascota);
        return "mascotas/editar";
    }

    // Guardar los cambios de una mascota editada, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarMascota(@PathVariable Long id, @ModelAttribute("mascota") Mascota mascota, @RequestParam("cliente.idCliente") Long idCliente, @RequestParam("estado") String estado) {
        Mascota mascotaActualizado = mascotaService.buscarPorId(id).orElse(null);
        mascotaActualizado.setDni(mascota.getDni());
        mascotaActualizado.setCodigo(mascota.getCodigo());
        mascotaActualizado.setNombres(mascota.getNombres());
        mascotaActualizado.setApellidos(mascota.getApellidos());
        mascotaActualizado.setEdad(mascota.getEdad());
        mascotaActualizado.setSexo(mascota.getSexo());
        mascotaActualizado.setRaza(mascota.getRaza());
        mascotaActualizado.setCarnetVacunas(mascota.getCarnetVacunas());
        mascotaActualizado.setEstadoClinico(mascota.getEstadoClinico());
        mascotaActualizado.setEstado(estado);
        Cliente cliente = clienteService.buscarPorId(idCliente).orElseThrow(() -> new IllegalArgumentException("Cliente no existe: " + idCliente));
        mascotaActualizado.setCliente(cliente);
        mascotaService.guardar(mascotaActualizado);
        return "redirect:/mascotas/listar";
    }
    
    // Eliminar una mascota, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminarMascota(@PathVariable Long id) {
        mascotaService.eliminar(id);
        return "redirect:/mascotas/listar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/export/excel")
    public void exportInventariosExcel(HttpServletResponse response) throws IOException {
        String filename = "mascotas_" + java.time.LocalDate.now() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<Mascota> mascotas = mascotaService.listar();

        try (var workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = workbook.createSheet("Mascotas");

            // Estilo header
            var headerStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Header
            String[] headers = {"ID", "Cliente", "DNI", "Código", "Nombres", "Apellidos", "Edad", "Sexo", "Raza", "Carnet Vacunas", "Estado Clinico", "Estado"};
            var headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Body
            int rowIdx = 1;
            for (Mascota m : mascotas) {
                var row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(m.getIdMascota());
                row.createCell(1).setCellValue(m.getCliente() != null ? String.valueOf(m.getCliente().getIdCliente()) : "");
                row.createCell(2).setCellValue(m.getDni());
                row.createCell(3).setCellValue(safe(m.getCodigo()));
                row.createCell(4).setCellValue(safe(m.getNombres()));
                row.createCell(5).setCellValue(safe(m.getApellidos()));
                row.createCell(6).setCellValue(m.getEdad());
                row.createCell(7).setCellValue(safe(m.getSexo()));
                row.createCell(8).setCellValue(safe(m.getRaza()));
                row.createCell(9).setCellValue(safe(m.getCarnetVacunas()));
                row.createCell(10).setCellValue(safe(m.getEstadoClinico()));
                row.createCell(11).setCellValue(safe(m.getEstado()));
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
