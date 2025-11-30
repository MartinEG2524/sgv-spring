package com.garritas.sgv.controller;

import com.garritas.sgv.model.Cita;
import com.garritas.sgv.model.Mascota;
import com.garritas.sgv.model.Servicio;
import com.garritas.sgv.model.Veterinario;
import com.garritas.sgv.service.CitaService;
import com.garritas.sgv.service.MascotaService;
import com.garritas.sgv.service.ServicioService;
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
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private VeterinarioService veterinarioService;
    
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA') or hasRole('ROLE_VETERINARIO')")
    @GetMapping("listar")
    public String listar(Model model) {
        List<Cita> citas = citaService.listar();
        model.addAttribute("citas", citas);
        return "citas/listar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VETERINARIO')")
    @GetMapping("ver/{id}")
    public String buscarCita(@PathVariable Long id, Model model) {
        Cita cita = citaService.buscarPorId(id).orElse(null);
        model.addAttribute("cita", cita);
        return "citas/ver";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VETERINARIO')")
    @GetMapping("/registrar")
    public String registrarCita(Model model) {
        if (!model.containsAttribute("cita")) {
            model.addAttribute("cita", new Cita());
        }
        return "citas/registrar";
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @PostMapping("/registrar")
    public String guardarCita(@ModelAttribute("cita") Cita cita, RedirectAttributes ra) {
        if (citaService.existeCitaEnHorario(cita.getFecha(), cita.getHora())) {
            ra.addFlashAttribute("errorMessage", "La Cita '" + cita.getFecha() + "-" + cita.getHora() + "' ya está registrado.");
            return "redirect:/citas/registrar";
        }
        cita.getMascota().getIdMascota();
        cita.getServicio().getIdServicio();
        cita.getVeterinario().getIdVeterinario();
        cita.setEstado("Activo");
        citaService.guardar(cita);
        return "redirect:/citas/listar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminarCita(@PathVariable Long id) {
        citaService.eliminar(id);
        return "redirect:/citas/listar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VETERINARIO')")
    @GetMapping("/editar/{id}")
    public String editarCita(@PathVariable Long id, Model model) {
        Cita cita = citaService.buscarPorId(id).orElse(null);
        model.addAttribute("cita", cita);
        return "citas/editar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VETERINARIO')")
    @PostMapping("/editar/{id}")
    public String actualizarCita(@PathVariable Long id, @ModelAttribute("cita") Cita cita, @RequestParam("mascota.idMascota") Long idMascota, @RequestParam("servicio.idServicio") Long idServicio,
        @RequestParam("veterinario.idVeterinario") Long idVeterinario, @RequestParam("estado") String estado) {
        Cita citaActualizada = citaService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Cita no encontrada: " + id));
        citaActualizada.setFecha(cita.getFecha());
        citaActualizada.setHora(cita.getHora());
        citaActualizada.setEstado(estado);
        Mascota mascota = mascotaService.buscarPorId(idMascota).orElseThrow(() -> new IllegalArgumentException("Mascota no existe: " + idMascota));
        citaActualizada.setMascota(mascota);
        Servicio servicio = servicioService.buscarPorId(idServicio).orElseThrow(() -> new IllegalArgumentException("Servicio no existe: " + idServicio));
        citaActualizada.setServicio(servicio);
        Veterinario veterinario = veterinarioService.buscarPorId(idVeterinario).orElseThrow(() -> new IllegalArgumentException("Veterinario no existe: " + idVeterinario));
        citaActualizada.setVeterinario(veterinario);
        citaService.actualizar(citaActualizada);
        return "redirect:/citas/listar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/export/excel")
    public void exportCitasExcel(HttpServletResponse response) throws IOException {
        String filename = "citas_" + java.time.LocalDate.now() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<Cita> citas = citaService.listar();

        try (var workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = workbook.createSheet("Citas");

            var headerStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            var createHelper = workbook.getCreationHelper();

            var dateCellStyle = workbook.createCellStyle();
            short dateFormat = createHelper.createDataFormat().getFormat("dd-MM-yyyy");
            dateCellStyle.setDataFormat(dateFormat);

            var timeCellStyle = workbook.createCellStyle();
            short timeFormat = createHelper.createDataFormat().getFormat("HH:mm");
            timeCellStyle.setDataFormat(timeFormat);

            String[] headers = {"ID", "Mascota", "Veterinario", "Servicio", "Fecha", "Hora", "Estado"};
            var headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Cita c : citas) {
                var row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.getIdCita());
                row.createCell(1).setCellValue(c.getMascota() != null ? String.valueOf(c.getMascota().getIdMascota()) : "");
                row.createCell(2).setCellValue(c.getVeterinario() != null ? String.valueOf(c.getVeterinario().getIdVeterinario()) : "");
                row.createCell(3).setCellValue(c.getServicio() != null ? String.valueOf(c.getServicio().getIdServicio()) : "");
                var fechaCell = row.createCell(4);
                if (c.getFecha() != null) {
                    fechaCell.setCellValue(c.getFecha());
                    fechaCell.setCellStyle(dateCellStyle);
                } else {
                    fechaCell.setBlank();
                }
                row.createCell(5).setCellValue(safe(c.getHora().toString()));
                row.createCell(6).setCellValue(safe(c.getEstado()));
            }
            
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            workbook.write(response.getOutputStream());
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
