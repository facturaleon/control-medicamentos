package com.prueba.medicamentos.config;

import com.prueba.medicamentos.model.Medicamento;
import com.prueba.medicamentos.repository.MedicamentoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner cargarMedicamentos(MedicamentoRepository repository) {
        return args -> {
            String[] nombres = {
                "Paracetamol", "Ibuprofeno", "Amoxicilina",
                "Loratadina", "Omeprazol", "Metformina"
            };
            for (String nombre : nombres) {
                if (!repository.existsByNombreIgnoreCase(nombre)) {
                    repository.save(new Medicamento(nombre));
                }
            }
        };
    }
}
