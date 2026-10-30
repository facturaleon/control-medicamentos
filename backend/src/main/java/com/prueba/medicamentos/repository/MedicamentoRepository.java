package com.prueba.medicamentos.repository;

import com.prueba.medicamentos.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
