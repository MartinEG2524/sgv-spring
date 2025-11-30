package com.garritas.sgv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@Entity
@Table(name = "cola_atencion")
public class ColaAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cola")
    private Long idCola;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false)
    private Mascota mascota;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(nullable = false)
    private Boolean atendido;

    private String estado;
}
