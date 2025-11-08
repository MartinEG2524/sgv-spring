package com.garritas.sgv.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cargos")
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    private String nombre;
    private String descripcion;
    private String estado;

    @OneToMany(mappedBy = "idCargo", fetch = FetchType.LAZY)
    private List<Usuario> usuarios;
}