package com.prueba.medicamentos.model;

import javax.persistence.*;

@Entity
@Table(name = "detalle_reporte",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_medicamento_reporte",
           columnNames = {"reporte_id", "medicamento_id"}))
public class DetalleReporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reporte_id", nullable = false)
    private Reporte reporte;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @Column(nullable = false)
    private Integer ingreso;

    @Column(nullable = false)
    private Integer egreso;

    public DetalleReporte() {}

    public Long getId() { return id; }
    public Reporte getReporte() { return reporte; }
    public void setReporte(Reporte reporte) { this.reporte = reporte; }
    public Medicamento getMedicamento() { return medicamento; }
    public void setMedicamento(Medicamento medicamento) { this.medicamento = medicamento; }
    public Integer getIngreso() { return ingreso; }
    public void setIngreso(Integer ingreso) { this.ingreso = ingreso; }
    public Integer getEgreso() { return egreso; }
    public void setEgreso(Integer egreso) { this.egreso = egreso; }
}
