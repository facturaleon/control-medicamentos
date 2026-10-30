package com.prueba.medicamentos.model;

import javax.persistence.*;

@Entity
@Table(name = "reporte",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_reporte_mensual",
           columnNames = {"unicodigo", "mes", "anio"}))
public class Reporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String unicodigo;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false, length = 20)
    private String estado = "REGISTRADO";

    public Reporte() {}

    public Long getId() { return id; }
    public String getUnicodigo() { return unicodigo; }
    public void setUnicodigo(String unicodigo) { this.unicodigo = unicodigo; }
    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    public String getEstado() { return estado; }
}
