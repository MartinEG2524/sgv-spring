package com.garritas.sgv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@SqlResultSetMapping(
    name = "UsuarioResultados",
    entities = {
        @EntityResult(entityClass = Usuario.class, fields = {
            @FieldResult(name = "idUsuario", column = "id_usuario"),
            @FieldResult(name = "codigo", column = "codigo")
        }),
        @EntityResult(entityClass = Cargo.class, fields = {
            @FieldResult(name = "nombre", column = "nombre")
        }),
        @EntityResult(entityClass = Veterinario.class, fields = {
            @FieldResult(name = "idVeterinario", column = "id_veterinario"),
            @FieldResult(name = "nombres", column = "nombres"),
            @FieldResult(name = "apellidos", column = "apellidos"),
            @FieldResult(name = "dni", column = "dni"),
            @FieldResult(name = "correo", column = "correo"),
            @FieldResult(name = "especialidad", column = "especialidad"),
            @FieldResult(name = "sexo", column = "sexo"),
            @FieldResult(name = "fechaNacimiento", column = "fecha_nacimiento"),
            @FieldResult(name = "celular", column = "celular"),
            @FieldResult(name = "pais", column = "pais"),
            @FieldResult(name = "provincia", column = "provincia"),
            @FieldResult(name = "distrito", column = "distrito")
        }),
        @EntityResult(entityClass = Cliente.class, fields = {
            @FieldResult(name = "idCliente", column = "id_cliente"),
            @FieldResult(name = "nombres", column = "nombres"),
            @FieldResult(name = "apellidos", column = "apellidos"),
            @FieldResult(name = "dni", column = "dni"),
            @FieldResult(name = "correo", column = "correo"),
            @FieldResult(name = "direccion", column = "direccion"),
            @FieldResult(name = "sexo", column = "sexo"),
            @FieldResult(name = "fechaNacimiento", column = "fecha_nacimiento"),
            @FieldResult(name = "celular", column = "celular"),
            @FieldResult(name = "pais", column = "pais"),
            @FieldResult(name = "provincia", column = "provincia"),
            @FieldResult(name = "distrito", column = "distrito")
        })
    }
)
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String contrasena;
    
    @Column(nullable = false)
    private String estado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Cargo idCargo;

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Cliente cliente;

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Veterinario veterinario;
}
