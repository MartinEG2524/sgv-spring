package com.garritas.sgv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "detalle_historial_inventario")
public class DetalleHistorialInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_historial", nullable = false)
    private HistorialClinico historial;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Inventario producto;

    @Column(name = "cantidad_utilizada")
    private Integer cantidadUtilizada;
}
