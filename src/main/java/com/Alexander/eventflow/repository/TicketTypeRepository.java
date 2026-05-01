package com.Alexander.eventflow.repository;

import com.Alexander.eventflow.model.TicketType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {

    // Todos los tipos de ticket de un evento
    List<TicketType> findByEventId(Long eventId);

    // Buscar un ticket type específico dentro de un evento
    Optional<TicketType> findByIdAndEventId(Long id, Long eventId);

    // Verificar si hay capacidad disponible
    boolean existsByIdAndAvailableCapacityGreaterThan(Long id, int capacity);

    // Bloqueo pesimista — para la compra concurrente
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TicketType t WHERE t.id = :id")
    Optional<TicketType> findByIdForUpdate(@Param("id") Long id);
}