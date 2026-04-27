package com.Alexander.eventflow.repository;

import com.Alexander.eventflow.model.Event;
import com.Alexander.eventflow.model.User;
import com.Alexander.eventflow.model.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Todos los eventos publicados — paginados
    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    // Eventos de un organizador específico
    List<Event> findByOrganizer(User organizer);

    // Buscar por categoría y estado
    Page<Event> findByStatusAndCategory(EventStatus status, String category, Pageable pageable);

    // Verificar si un evento pertenece a un organizador
    boolean existsByIdAndOrganizer(Long id, User organizer);

    // Buscar evento por id + su organizador en una sola query — evita N+1
    @Query("SELECT e FROM Event e JOIN FETCH e.organizer WHERE e.id = :id")
    Optional<Event> findByIdWithOrganizer(@Param("id") Long id);

    // Buscar eventos con un status específico además de publicados por título — búsqueda parcial

    @Query("SELECT e FROM Event e WHERE e.status = :status " +
            "AND LOWER(e.title) LIKE LOWER(CONCAT('%', :term, '%'))")
    Page<Event> searchByTerm(@Param("term") String term,
                             @Param("status") EventStatus status,
                             Pageable pageable);
}