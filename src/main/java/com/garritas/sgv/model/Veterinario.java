package com.garritas.sgv.model;

import jakarta.persistence.*;

import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "veterinarios")
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_veterinario")
    private Long idVeterinario;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    private String nombres;
    private String apellidos;
    private Integer dni;
    private String correo;
    private String especialidad;
    private String sexo;

    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    private Integer celular;
    private String pais;
    private String provincia;
    private String distrito;
    private String estado;
}
