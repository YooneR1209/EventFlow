package com.Alexander.eventflow.service;

import com.Alexander.eventflow.dto.request.ChangeStatusRequest;
import com.Alexander.eventflow.dto.request.CreateEventRequest;
import com.Alexander.eventflow.dto.request.UpdateEventRequest;
import com.Alexander.eventflow.dto.response.EventDetailDTO;
import com.Alexander.eventflow.dto.response.EventSummaryDTO;
import com.Alexander.eventflow.exception.BusinessException;
import com.Alexander.eventflow.exception.ResourceNotFoundException;
import com.Alexander.eventflow.mapper.EventMapper;
import com.Alexander.eventflow.model.Event;
import com.Alexander.eventflow.model.User;
import com.Alexander.eventflow.model.enums.EventStatus;
import com.Alexander.eventflow.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    // — Consultas públicas —

    public Page<EventSummaryDTO> findAllPublished(Pageable pageable) {
        return eventRepository
                .findByStatus(EventStatus.PUBLISHED, pageable)
                .map(eventMapper::toSummaryDTO);
    }

    public Page<EventSummaryDTO> findByCategory(String category, Pageable pageable) {
        return eventRepository
                .findByStatusAndCategory(EventStatus.PUBLISHED, category, pageable)
                .map(eventMapper::toSummaryDTO);
    }

    public Page<EventSummaryDTO> search(String term, Pageable pageable) {
        return eventRepository
                .searchByTerm(term, EventStatus.PUBLISHED, pageable)
                .map(eventMapper::toSummaryDTO);
    }

    public EventDetailDTO findById(Long id) {
        Event event = eventRepository.findByIdWithOrganizer(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", id));
        return eventMapper.toDetailDTO(event);
    }

    // — Consultas del organizador —

    public List<EventSummaryDTO> findMyEvents(User organizer) {
        return eventRepository.findByOrganizer(organizer)
                .stream()
                .map(eventMapper::toSummaryDTO)
                .toList();
    }

    // — Operaciones del organizador —

    @Transactional
    public EventDetailDTO create(CreateEventRequest request, User organizer) {
        Event event = eventMapper.toEntity(request, organizer);
        Event saved = eventRepository.save(event);
        return eventMapper.toDetailDTO(saved);
    }

    @Transactional
    public EventDetailDTO update(Long id, UpdateEventRequest request, User organizer) {

        Event event = findEventAndVerifyOwnership(id, organizer);

        if (event.getStatus() != EventStatus.DRAFT) {
            throw new BusinessException(
                    "Solo puedes editar eventos en estado DRAFT"
            );
        }

        if (request.title() != null) event.setTitle(request.title());
        if (request.description() != null) event.setDescription(request.description());
        if (request.location() != null) event.setLocation(request.location());
        if (request.eventDate() != null) event.setEventDate(request.eventDate());
        if (request.registrationDeadline() != null)
            event.setRegistrationDeadline(request.registrationDeadline());
        if (request.category() != null) event.setCategory(request.category());

        Event saved = eventRepository.save(event);
        return eventMapper.toDetailDTO(saved);
    }

    @Transactional
    public EventDetailDTO changeStatus(Long id, ChangeStatusRequest request, User organizer) {

        Event event = findEventAndVerifyOwnership(id, organizer);
        validateStatusTransition(event.getStatus(), request.status());
        event.setStatus(request.status());

        Event saved = eventRepository.save(event);
        return eventMapper.toDetailDTO(saved);
    }

    @Transactional
    public void delete(Long id, User organizer) {

        Event event = findEventAndVerifyOwnership(id, organizer);

        if (event.getStatus() != EventStatus.DRAFT) {
            throw new BusinessException(
                    "Solo puedes eliminar eventos en estado DRAFT"
            );
        }

        eventRepository.delete(event);
    }

    // — Métodos privados auxiliares —

    private Event findEventAndVerifyOwnership(Long id, User organizer) {
        Event event = eventRepository.findByIdWithOrganizer(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", id));

        if (!event.getOrganizer().getId().equals(organizer.getId())) {
            throw new BusinessException(
                    "No tienes permiso para modificar este evento"
            );
        }

        return event;
    }

    private void validateStatusTransition(EventStatus current, EventStatus next) {
        boolean valid = switch (current) {
            case DRAFT     -> next == EventStatus.PUBLISHED
                    || next == EventStatus.CANCELLED;
            case PUBLISHED -> next == EventStatus.SOLD_OUT
                    || next == EventStatus.CLOSED
                    || next == EventStatus.CANCELLED;
            case SOLD_OUT  -> next == EventStatus.CLOSED
                    || next == EventStatus.CANCELLED;
            case CLOSED, CANCELLED -> false;
        };

        if (!valid) {
            throw new BusinessException(
                    "Transición de estado inválida: " + current + " → " + next
            );
        }
    }
}