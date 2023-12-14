package io.bootify.proyecto_batch.service;

import io.bootify.proyecto_batch.domain.Transacciones;
import io.bootify.proyecto_batch.model.TransaccionesDTO;
import io.bootify.proyecto_batch.repos.TransaccionesRepository;
import io.bootify.proyecto_batch.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class TransaccionesService {

    private final TransaccionesRepository transaccionesRepository;

    public TransaccionesService(final TransaccionesRepository transaccionesRepository) {
        this.transaccionesRepository = transaccionesRepository;
    }

    public List<TransaccionesDTO> findAll() {
        final List<Transacciones> transaccioneses = transaccionesRepository.findAll(Sort.by("id"));
        return transaccioneses.stream()
                .map(transacciones -> mapToDTO(transacciones, new TransaccionesDTO()))
                .toList();
    }

    public TransaccionesDTO get(final Long id) {
        return transaccionesRepository.findById(id)
                .map(transacciones -> mapToDTO(transacciones, new TransaccionesDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final TransaccionesDTO transaccionesDTO) {
        final Transacciones transacciones = new Transacciones();
        mapToEntity(transaccionesDTO, transacciones);
        return transaccionesRepository.save(transacciones).getId();
    }

    public void update(final Long id, final TransaccionesDTO transaccionesDTO) {
        final Transacciones transacciones = transaccionesRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(transaccionesDTO, transacciones);
        transaccionesRepository.save(transacciones);
    }

    public void delete(final Long id) {
        transaccionesRepository.deleteById(id);
    }

    private TransaccionesDTO mapToDTO(final Transacciones transacciones,
            final TransaccionesDTO transaccionesDTO) {
        transaccionesDTO.setId(transacciones.getId());
        transaccionesDTO.setFecha(transacciones.getFecha());
        transaccionesDTO.setCantidad(transacciones.getCantidad());
        transaccionesDTO.setTipo(transacciones.getTipo());
        transaccionesDTO.setCuentaOrigen(transacciones.getCuentaOrigen());
        transaccionesDTO.setCuentaDestino(transacciones.getCuentaDestino());
        return transaccionesDTO;
    }

    private Transacciones mapToEntity(final TransaccionesDTO transaccionesDTO,
            final Transacciones transacciones) {
        transacciones.setFecha(transaccionesDTO.getFecha());
        transacciones.setCantidad(transaccionesDTO.getCantidad());
        transacciones.setTipo(transaccionesDTO.getTipo());
        transacciones.setCuentaOrigen(transaccionesDTO.getCuentaOrigen());
        transacciones.setCuentaDestino(transaccionesDTO.getCuentaDestino());
        return transacciones;
    }

}
