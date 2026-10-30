package com.prueba.medicamentos.controller;

import com.prueba.medicamentos.dto.ReporteRequest;
import com.prueba.medicamentos.model.Reporte;
import com.prueba.medicamentos.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "http://localhost:4200")
public class ReporteController {
    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Reporte> listar() {
        return service.listar();
    }

    @GetMapping("/stock")
    public Map<String, Long> stock(@RequestParam String unicodigo,
                                   @RequestParam Long medicamentoId,
                                   @RequestParam int mes,
                                   @RequestParam int anio) {
        return Collections.singletonMap(
            "stockAnterior", service.obtenerStock(unicodigo, medicamentoId, mes, anio));
    }

    @PostMapping
    public Reporte guardar(@Valid @RequestBody ReporteRequest request) {
        return service.guardar(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
