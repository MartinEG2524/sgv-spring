package com.garritas.sgv.controller;

import com.garritas.sgv.model.Cliente;
import com.garritas.sgv.model.Usuario;
import com.garritas.sgv.service.ClienteService;
import com.garritas.sgv.service.UsuarioService;

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
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    // Vista de todos los clientes, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("listar")
    public String listar(Model model) {
        List<Cliente> clientes = clienteService.listar();
        model.addAttribute("clientes", clientes);
        return "clientes/listar";
    }

    // Vista para ver un cliente específico, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("ver/{id}")
    public String buscarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id).orElse(null);
        model.addAttribute("cliente", cliente);
        return "clientes/ver";
    }

    // Vista para agregar un nuevo cliente, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @GetMapping("/registrar")
    public String registarCliente(Model model) {
        if (!model.containsAttribute("cliente")) {
            model.addAttribute("cliente", new Cliente());
        }
        return "clientes/registrar";
    }

    // Guardar un nuevo cliente, solo accesible para ADMIN y RECEPCIONISTA
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA')")
    @PostMapping("/registrar")
    public String guardarCliente(@ModelAttribute("cliente") Cliente cliente, RedirectAttributes ra) {
        if (clienteService.buscarPorDni(cliente.getDni()).isPresent()) {
            ra.addFlashAttribute("errorMessage", "El DNI '" + cliente.getDni() + "' ya está registrado.");
            return "redirect:/clientes/registrar";
        }
        cliente.getUsuario().getIdUsuario();
        cliente.setEstado("Activo");
        clienteService.guardar(cliente);
        return "redirect:/clientes/listar";
    }

    // Vista para editar un cliente existente
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA') or hasRole('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id).orElse(null);
        model.addAttribute("cliente", cliente);
        return "clientes/editar";
    }

    // Guardar los cambios de un cliente editado
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECEPCIONISTA') or hasRole('ROLE_ADMIN')")
    @PostMapping("/editar/{id}")
    public String actualizarCliente(@PathVariable Long id, @ModelAttribute("cliente") Cliente cliente, @RequestParam("usuario.idUsuario") Long idUsuario, @RequestParam("estado") String estado) {
        Cliente clienteActualizado = clienteService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
        clienteActualizado.setNombres(cliente.getNombres());
        clienteActualizado.setApellidos(cliente.getApellidos());
        clienteActualizado.setDni(cliente.getDni());
        clienteActualizado.setCorreo(cliente.getCorreo());
        clienteActualizado.setDireccion(cliente.getDireccion());
        clienteActualizado.setSexo(cliente.getSexo());
        clienteActualizado.setFechaNacimiento(cliente.getFechaNacimiento());
        clienteActualizado.setCelular(cliente.getCelular());
        clienteActualizado.setPais(cliente.getPais());
        clienteActualizado.setProvincia(cliente.getProvincia());
        clienteActualizado.setDistrito(cliente.getDistrito());
        clienteActualizado.setEstado(cliente.getEstado());
        Usuario usuario = usuarioService.buscarPorId(idUsuario).orElseThrow(() -> new IllegalArgumentException("Usuario no existe: " + idUsuario));
        clienteActualizado.setUsuario(usuario);
        clienteService.actualizar(clienteActualizado);
        return "redirect:/clientes/listar";
    }

    // Eliminar un cliente, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id) {
        clienteService.eliminar(id);
        return "redirect:/clientes/listar";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/export/excel")
    public void exportClientesExcel(HttpServletResponse response) throws IOException {
        String filename = "clientes_" + java.time.LocalDate.now() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<Cliente> clientes = clienteService.listar();

        try (var workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            var sheet = workbook.createSheet("Clientes");

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
            String[] headers = {"ID", "Usuario", "Nombres", "Apellidos", "DNI", "Correo", "Direccion", "Sexo", "Fecha Nacimiento", "Celular", "Pais", "Provincia", "Distrito", "Estado"};
            var headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Body
            int rowIdx = 1;
            for (Cliente c : clientes) {
                var row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.getIdCliente());
                row.createCell(1).setCellValue(c.getUsuario() != null ? String.valueOf(c.getUsuario().getIdUsuario()) : "");
                row.createCell(2).setCellValue(safe(c.getNombres()));
                row.createCell(3).setCellValue(safe(c.getApellidos()));
                row.createCell(4).setCellValue(c.getDni());
                row.createCell(5).setCellValue(safe(c.getCorreo()));
                row.createCell(6).setCellValue(safe(c.getDireccion()));
                row.createCell(7).setCellValue(safe(c.getSexo()));
                var fechaCell = row.createCell(8);
                if (c.getFechaNacimiento() != null) {
                    fechaCell.setCellValue(c.getFechaNacimiento());
                    fechaCell.setCellStyle(dateCellStyle);
                } else {
                    fechaCell.setBlank();
                }
                row.createCell(9).setCellValue(c.getCelular());
                row.createCell(10).setCellValue(safe(c.getPais()));
                row.createCell(11).setCellValue(safe(c.getProvincia()));
                row.createCell(12).setCellValue(safe(c.getDistrito()));
                row.createCell(13).setCellValue(safe(c.getEstado()));
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
