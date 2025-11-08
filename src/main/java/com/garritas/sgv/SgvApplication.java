package com.garritas.sgv;

import com.garritas.sgv.model.Cargo;
import com.garritas.sgv.model.Usuario;
import com.garritas.sgv.repository.CargoRepository;
import com.garritas.sgv.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SgvApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgvApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(UsuarioRepository usuarioRepository, CargoRepository cargoRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Crear cargos si no existen
            Cargo cargoAdmin = cargoRepository.findByNombre("ADMIN")
                    .orElseGet(() -> {
                        Cargo c = new Cargo();
                        c.setNombre("ADMIN");
                        c.setDescripcion("Acceso total al sistema");
                        c.setEstado("Activo");
                        return cargoRepository.save(c);
                    });

            Cargo cargoVeterinario = cargoRepository.findByNombre("VETERINARIO")
                    .orElseGet(() -> {
                        Cargo c = new Cargo();
                        c.setNombre("VETERINARIO");
                        c.setDescripcion("Encargado de atender mascotas");
                        c.setEstado("Activo");
                        return cargoRepository.save(c);
                    });

            Cargo cargoRecepcionista = cargoRepository.findByNombre("RECEPCIONISTA")
                    .orElseGet(() -> {
                        Cargo c = new Cargo();
                        c.setNombre("RECEPCIONISTA");
                        c.setDescripcion("Encargado de la gestión");
                        c.setEstado("Activo");
                        return cargoRepository.save(c);
                    });

            Cargo cargoCliente = cargoRepository.findByNombre("CLIENTE")
                    .orElseGet(() -> {
                        Cargo c = new Cargo();
                        c.setNombre("CLIENTE");
                        c.setDescripcion("Usuario con mascotas registradas");
                        c.setEstado("Activo");
                        return cargoRepository.save(c);
                    });

            // Crear usuarios con contraseñas encriptadas
            if (usuarioRepository.findByCodigo("admin").isEmpty()) {
                Usuario adminUser = new Usuario();
                adminUser.setCodigo("admin");
                //adminUser.setContrasena(passwordEncoder.encode("adminpass"));
                String adminPassword = passwordEncoder.encode("adminpass");
                adminUser.setContrasena(adminPassword);
                adminUser.setIdCargo(cargoAdmin);
                usuarioRepository.save(adminUser);
                System.out.println("Usuario 'admin' creado. Contraseña encriptada: " + adminPassword);
            }

            if (usuarioRepository.findByCodigo("veterinario").isEmpty()) {
                Usuario vetUser = new Usuario();
                vetUser.setCodigo("veterinario");
                vetUser.setContrasena(passwordEncoder.encode("vetpass"));
                vetUser.setIdCargo(cargoVeterinario);
                usuarioRepository.save(vetUser);
                System.out.println("Usuario 'veterinario' creado.");
            }

            if (usuarioRepository.findByCodigo("recepcionista").isEmpty()) {
                Usuario vetUser = new Usuario();
                vetUser.setCodigo("recepcionista");
                vetUser.setContrasena(passwordEncoder.encode("recepass"));
                vetUser.setIdCargo(cargoRecepcionista);
                usuarioRepository.save(vetUser);
                System.out.println("Usuario 'recepcionista' creado.");
            }

            if (usuarioRepository.findByCodigo("cliente").isEmpty()) {
                Usuario cliUser = new Usuario();
                cliUser.setCodigo("cliente");
                //cliUser.setContrasena(passwordEncoder.encode("clipass"));
                String cliPassword = passwordEncoder.encode("clipass");
                cliUser.setContrasena(cliPassword);
                cliUser.setIdCargo(cargoCliente);
                usuarioRepository.save(cliUser);
                System.out.println("Usuario 'cliente' creado. Contraseña encriptada: " + cliPassword);
            }
        };
    }
}
