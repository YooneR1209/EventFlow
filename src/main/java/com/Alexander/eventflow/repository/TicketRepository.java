package com.Alexander.eventflow.repository;

import com.Alexander.eventflow.model.Ticket;
import com.Alexander.eventflow.model.TicketType;
import com.Alexander.eventflow.model.User;
import com.Alexander.eventflow.model.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByUser(User user);

    // Tickets activos de cierto estatus
    List<Ticket> findByUserAndStatus(User user, TicketStatus status);

    // Ticket por id verificando que pertenece al usuario
    Optional<Ticket> findByIdAndUser(Long id, User user);

    // Verificar si el usuario ya tiene un ticket activo para este tipo
    boolean existsByUserAndTicketTypeIdAndStatus(
            User user, Long ticketTypeId, TicketStatus status);

    // Todos los tickets de un evento — para el organizador
    @Query("SELECT t FROM Ticket t " +
            "JOIN FETCH t.user " +
            "JOIN FETCH t.ticketType tt " +
            "WHERE tt.event.id = :eventId " +
            "AND t.status = :status")
    List<Ticket> findByEventIdAndStatus(
            @Param("eventId") Long eventId,
            @Param("status") TicketStatus status);

    // Bloqueo pesimista — para la compra concurrente
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TicketType t WHERE t.id = :id")
    TicketType findTicketTypeForUpdate(@Param("id") Long id);

    // Contar tickets vendidos por tipo
    @Query("SELECT COUNT(t) FROM Ticket t " +
            "WHERE t.ticketType.id = :ticketTypeId " +
            "AND t.status != :status")
    long countByTicketTypeIdAndStatusNot(
            @Param("ticketTypeId") Long ticketTypeId,
            @Param("status") TicketStatus status);
}