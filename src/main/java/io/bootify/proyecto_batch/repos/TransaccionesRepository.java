package io.bootify.proyecto_batch.repos;

import io.bootify.proyecto_batch.domain.Transacciones;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TransaccionesRepository extends JpaRepository<Transacciones, Long> {
}
