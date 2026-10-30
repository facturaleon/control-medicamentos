package com.prueba.medicamentos.service;

import com.prueba.medicamentos.dto.DetalleRequest;
import com.prueba.medicamentos.dto.ReporteRequest;
import com.prueba.medicamentos.model.DetalleReporte;
import com.prueba.medicamentos.model.Medicamento;
import com.prueba.medicamentos.model.Reporte;
import com.prueba.medicamentos.repository.DetalleReporteRepository;
import com.prueba.medicamentos.repository.MedicamentoRepository;
import com.prueba.medicamentos.repository.ReporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReporteService {
    private final ReporteRepository reporteRepo;
    private final MedicamentoRepository medicamentoRepo;
    private final DetalleReporteRepository detalleRepo;

    public ReporteService(ReporteRepository reporteRepo,
                          MedicamentoRepository medicamentoRepo,
                          DetalleReporteRepository detalleRepo) {
        this.reporteRepo = reporteRepo;
        this.medicamentoRepo = medicamentoRepo;
        this.detalleRepo = detalleRepo;
    }

    public List<Reporte> listar() {
        return reporteRepo.findAllByOrderByAnioDescMesDescIdDesc();
    }

    public List<Medicamento> listarMedicamentos() {
        return medicamentoRepo.findAll();
    }

    public Long obtenerStock(String unicodigo, Long medicamentoId, int mes, int anio) {
        validarPeriodo(mes, anio);
        if (!medicamentoRepo.existsById(medicamentoId)) {
            throw new IllegalArgumentException("Medicamento no encontrado");
        }
        return detalleRepo.calcularStockAnterior(medicamentoId, unicodigo.trim(), anio, mes);
    }

    @Transactional
    public Reporte guardar(ReporteRequest request) {
        if (request.unicodigo == null || request.unicodigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El UNICODIGO es obligatorio");
        }
        if (request.mes == null || request.anio == null) {
            throw new IllegalArgumentException("El mes y año son obligatorios");
        }
        validarPeriodo(request.mes, request.anio);

        String unicodigo = request.unicodigo.trim();
        if (reporteRepo.existsByUnicodigoAndMesAndAnio(unicodigo, request.mes, request.anio)) {
            throw new IllegalArgumentException("Ya existe un reporte para ese UNICODIGO y periodo");
        }
        if (request.medicamentos == null || request.medicamentos.isEmpty()) {
            throw new IllegalArgumentException("Agregue al menos un medicamento");
        }

        Set<Long> ids = new HashSet<>();
        // Validar todo antes de escribir datos en la base.
        for (DetalleRequest item : request.medicamentos) {
            if (item.medicamentoId == null || item.ingreso == null || item.egreso == null) {
                throw new IllegalArgumentException("Complete medicamento, ingreso y egreso");
            }
            if (item.ingreso < 0 || item.egreso < 0) {
                throw new IllegalArgumentException("Las cantidades no pueden ser negativas");
            }
            if (!ids.add(item.medicamentoId)) {
                throw new IllegalArgumentException("No puede repetir un medicamento en el reporte");
            }

            Medicamento medicamento = medicamentoRepo.findById(item.medicamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado"));

            Long stockAnterior = detalleRepo.calcularStockAnterior(
                medicamento.getId(), unicodigo, request.anio, request.mes);

            if (item.egreso > stockAnterior + item.ingreso) {
                throw new IllegalArgumentException(
                    "Stock insuficiente para " + medicamento.getNombre() +
                    ". Stock anterior: " + stockAnterior +
                    ", ingreso: " + item.ingreso);
            }
        }

        Reporte reporte = new Reporte();
        reporte.setUnicodigo(unicodigo);
        reporte.setMes(request.mes);
        reporte.setAnio(request.anio);
        reporte = reporteRepo.save(reporte);

        for (DetalleRequest item : request.medicamentos) {
            DetalleReporte detalle = new DetalleReporte();
            detalle.setReporte(reporte);
            detalle.setMedicamento(medicamentoRepo.findById(item.medicamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado")));
            detalle.setIngreso(item.ingreso);
            detalle.setEgreso(item.egreso);
            detalleRepo.save(detalle);
        }
        return reporte;
    }

    @Transactional
    public void eliminar(Long id) {
        Reporte reporte = reporteRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));
        detalleRepo.deleteAll(detalleRepo.findByReporteId(id));
        reporteRepo.delete(reporte);
    }

    private void validarPeriodo(int mes, int anio) {
        YearMonth seleccionado;
        try {
            seleccionado = YearMonth.of(anio, mes);
        } catch (Exception e) {
            throw new IllegalArgumentException("Mes o año inválido");
        }
        YearMonth minimo = YearMonth.of(2025, 7);
        YearMonth actual = YearMonth.now();
        if (seleccionado.isBefore(minimo) || seleccionado.isAfter(actual)) {
            throw new IllegalArgumentException(
                "Seleccione un periodo entre julio de 2025 y el mes actual");
        }
    }
}
