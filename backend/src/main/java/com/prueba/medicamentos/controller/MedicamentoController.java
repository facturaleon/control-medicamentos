package com.prueba.medicamentos.controller;

import com.prueba.medicamentos.model.Medicamento;
import com.prueba.medicamentos.service.ReporteService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
@CrossOrigin(origins = "http://localhost:4200")
public class MedicamentoController {
    private final ReporteService service;

    public MedicamentoController(ReporteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Medicamento> listar() {
        return service.listarMedicamentos();
    }
}
