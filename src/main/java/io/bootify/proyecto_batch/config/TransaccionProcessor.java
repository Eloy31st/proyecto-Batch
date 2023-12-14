package io.bootify.proyecto_batch.config;

import io.bootify.proyecto_batch.domain.Transacciones;
import org.springframework.batch.item.ItemProcessor;

public class TransaccionProcessor implements ItemProcessor<Transacciones, Transacciones> {

    @Override
    public Transacciones process(Transacciones item) throws Exception {
        return item;
    }
}
