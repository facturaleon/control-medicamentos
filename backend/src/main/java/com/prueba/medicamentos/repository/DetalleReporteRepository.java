package com.prueba.medicamentos.repository;

import com.prueba.medicamentos.model.DetalleReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DetalleReporteRepository extends JpaRepository<DetalleReporte, Long> {
    List<DetalleReporte> findByReporteId(Long reporteId);

    @Query("SELECT COALESCE(SUM(d.ingreso - d.egreso), 0) " +
           "FROM DetalleReporte d " +
           "WHERE d.medicamento.id = :medicamentoId " +
           "AND d.reporte.unicodigo = :unicodigo " +
           "AND (d.reporte.anio < :anio OR " +
           "(d.reporte.anio = :anio AND d.reporte.mes < :mes))")
    Long calcularStockAnterior(
        @Param("medicamentoId") Long medicamentoId,
        @Param("unicodigo") String unicodigo,
        @Param("anio") Integer anio,
        @Param("mes") Integer mes
    );

    @Query("SELECT COALESCE(SUM(d.ingreso - d.egreso), 0) " +
           "FROM DetalleReporte d " +
           "WHERE d.medicamento.id = :medicamentoId " +
           "AND d.reporte.unicodigo = :unicodigo")
    Long calcularStockActual(@Param("medicamentoId") Long medicamentoId,
                             @Param("unicodigo") String unicodigo);
}
