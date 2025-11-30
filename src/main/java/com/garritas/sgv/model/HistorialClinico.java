package com.garritas.sgv.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "historial_clinico")
public class HistorialClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Long idHistorial;

    @ManyToOne
    @JoinColumn(name = "id_mascota", nullable = false)
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "id_veterinario", nullable = false)
    private Veterinario veterinario;

    @Column(unique = true)
    private String codigo;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime fecha;

    private String diagnostico;

    private String tratamiento;

    private String estado;
}
