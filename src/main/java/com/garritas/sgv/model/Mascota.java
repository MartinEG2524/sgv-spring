package com.garritas.sgv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "mascotas")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mascota")
    private Long idMascota;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(unique = true)
    private Integer dni;

    @Column(unique = true)
    private String codigo;

    private String nombres;

    private String apellidos;

    private Integer edad;

    private String sexo;

    private String raza;

    @Column(name = "carnet_vacunas")
    private String carnetVacunas;

    @Column(name = "estado_clinico")
    private String estadoClinico;

    private String estado;
}
