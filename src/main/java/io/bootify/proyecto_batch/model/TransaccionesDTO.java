package io.bootify.proyecto_batch.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TransaccionesDTO {

    private Long id;

    @NotNull
    private LocalDate fecha;

    @NotNull
    private Double cantidad;

    @NotNull
    private TipoTrans tipo;

    @NotNull
    @Size(max = 255)
    private String cuentaOrigen;

    @NotNull
    @Size(max = 255)
    private String cuentaDestino;

}
