package io.bootify.proyecto_batch.config;

import io.bootify.proyecto_batch.domain.Transacciones;
import io.bootify.proyecto_batch.model.TipoTrans;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

public class CustomFieldSetMapper implements FieldSetMapper<Transacciones> {
    @Override
    public Transacciones mapFieldSet(FieldSet fieldSet) throws BindException {
        Transacciones transacciones = new Transacciones();

        Date fechaValue = fieldSet.readDate("fecha", "yyyy-MM-dd");
        transacciones.setFecha(fechaValue != null ? fechaValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null);

        Double cantidadValue = fieldSet.readDouble("cantidad");
        transacciones.setCantidad(cantidadValue != null ? cantidadValue : 0.0);

        transacciones.setTipo(TipoTrans.valueOf(fieldSet.readString("tipo")));
        transacciones.setCuentaOrigen(fieldSet.readString("cuentaOrigen"));
        transacciones.setCuentaDestino(fieldSet.readString("cuentaDestino"));
        transacciones.setDateCreated(OffsetDateTime.now());
        transacciones.setLastUpdated(OffsetDateTime.now());

        return transacciones;
    }
}
