package io.bootify.proyecto_batch.rest;

import io.bootify.proyecto_batch.model.TransaccionesDTO;
import io.bootify.proyecto_batch.service.TransaccionesService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/transaccioness", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransaccionesResource {

    private final TransaccionesService transaccionesService;

    public TransaccionesResource(final TransaccionesService transaccionesService) {
        this.transaccionesService = transaccionesService;
    }

    @GetMapping
    public ResponseEntity<List<TransaccionesDTO>> getAllTransaccioness() {
        return ResponseEntity.ok(transaccionesService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransaccionesDTO> getTransacciones(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(transaccionesService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createTransacciones(
            @RequestBody @Valid final TransaccionesDTO transaccionesDTO) {
        final Long createdId = transaccionesService.create(transaccionesDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateTransacciones(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final TransaccionesDTO transaccionesDTO) {
        transaccionesService.update(id, transaccionesDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteTransacciones(@PathVariable(name = "id") final Long id) {
        transaccionesService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
