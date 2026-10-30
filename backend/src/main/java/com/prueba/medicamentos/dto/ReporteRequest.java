package com.prueba.medicamentos.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import java.util.List;

public class ReporteRequest {
    @NotBlank
    @Size(max = 30)
    public String unicodigo;

    @NotNull
    @Min(1)
    public Integer mes;

    @NotNull
    @Min(2025)
    public Integer anio;

    @NotNull
    @Size(min = 1, message = "Agregue al menos un medicamento")
    @Valid
    public List<DetalleRequest> medicamentos;
}
