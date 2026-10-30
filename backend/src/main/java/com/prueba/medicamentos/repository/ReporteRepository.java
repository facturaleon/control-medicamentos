package com.prueba.medicamentos.repository;

import com.prueba.medicamentos.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    boolean existsByUnicodigoAndMesAndAnio(String unicodigo, Integer mes, Integer anio);
    List<Reporte> findAllByOrderByAnioDescMesDescIdDesc();
}
