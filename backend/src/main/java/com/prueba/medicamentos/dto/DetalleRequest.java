package com.prueba.medicamentos.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;

public class DetalleRequest {
    @NotNull
    public Long medicamentoId;

    @NotNull
    @Min(0)
    public Integer ingreso;

    @NotNull
    @Min(0)
    public Integer egreso;
}
