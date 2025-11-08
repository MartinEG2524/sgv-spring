package com.garritas.sgv.controller;

import com.garritas.sgv.model.Cargo;
import com.garritas.sgv.service.CargoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cargos")
public class CargoController {

    private final CargoService cargoService;

    public CargoController(CargoService cargoService) {
        this.cargoService = cargoService;
    }

    // Vista de listado de cargos, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public String listar(Model model) {
        List<Cargo> cargos = cargoService.listar();
        model.addAttribute("cargos", cargos);
        return "cargos/listar";
    }

    // Vista para ver un cargo específica, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public String buscarCargo(@PathVariable Long id, Model model) {
        Cargo cargo = cargoService.buscarPorId(id).orElse(null);
        model.addAttribute("cargo", cargo);
        return "cargos/ver";
    }

    // Vista para agregar un nuevo cargo, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/registrar")
    public String registrarCargo(Model model) {
        model.addAttribute("cargo", new Cargo());
        return "cargos/registrar";
    }

    // Guardar un nuevo cargo, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public String guardarCargo(@ModelAttribute Cargo cargo) {
        cargoService.guardar(cargo);
        return "redirect:/cargos";
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
    public String actualizarCargo(@PathVariable Long id, @ModelAttribute Cargo cargo) {
        cargo.setIdRol(id);
        cargoService.guardar(cargo);
        return "redirect:/cargos";
    }

    // Eliminar un cargo, solo accesible para ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminarCargo(@PathVariable Long id) {
        cargoService.eliminar(id);
        return "redirect:/cargos";
    }
}
