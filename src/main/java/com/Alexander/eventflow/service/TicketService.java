package com.Alexander.eventflow.service;

import com.Alexander.eventflow.dto.request.PurchaseTicketRequest;
import com.Alexander.eventflow.dto.response.TicketResponseDTO;
import com.Alexander.eventflow.exception.BusinessException;
import com.Alexander.eventflow.exception.InsufficientTicketsException;
import com.Alexander.eventflow.exception.ResourceNotFoundException;
import com.Alexander.eventflow.mapper.TicketMapper;
import com.Alexander.eventflow.model.Ticket;
import com.Alexander.eventflow.model.TicketType;
import com.Alexander.eventflow.model.User;
import com.Alexander.eventflow.model.enums.EventStatus;
import com.Alexander.eventflow.model.enums.TicketStatus;
import com.Alexander.eventflow.repository.TicketRepository;
import com.Alexander.eventflow.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketMapper ticketMapper;

    @Transactional
    public TicketResponseDTO purchase(PurchaseTicketRequest request, User user) {

        // 1. Obtener el TicketType con bloqueo pesimista
        TicketType ticketType = ticketRepository
                .findTicketTypeForUpdate(request.ticketTypeId());

        if (ticketType == null) {
            throw new ResourceNotFoundException(
                    "Tipo de ticket", request.ticketTypeId());
        }

        // 2. Verificar que el evento está publicado
        if (ticketType.getEvent().getStatus() != EventStatus.PUBLISHED) {
            throw new BusinessException(
                    "Solo puedes comprar tickets de eventos publicados");
        }

        // 3. Verificar que el registro no ha cerrado
        if (LocalDateTime.now().isAfter(ticketType.getEvent().getRegistrationDeadline())) {
            throw new BusinessException(
                    "El plazo de registro para este evento ha cerrado");
        }

        // 4. Verificar que el usuario no tiene ya un ticket activo de este tipo
        if (ticketRepository.existsByUserAndTicketTypeIdAndStatus(
                user, request.ticketTypeId(), TicketStatus.ACTIVE)) {
            throw new BusinessException(
                    "Ya tienes un ticket activo para este tipo");
        }

        // 5. Verificar capacidad disponible
        if (ticketType.getAvailableCapacity() <= 0) {
            throw new InsufficientTicketsException(1, ticketType.getAvailableCapacity());
        }

        // 6. Decrementar capacidad disponible
        ticketType.setAvailableCapacity(ticketType.getAvailableCapacity() - 1);
        ticketTypeRepository.save(ticketType);

        // 7. Verificar si el evento se agotó
        if (ticketType.getAvailableCapacity() == 0) {
            checkAndMarkSoldOut(ticketType);
        }

        // 8. Crear y guardar el ticket
        Ticket ticket = Ticket.builder()
                .user(user)
                .ticketType(ticketType)
                .build();

        Ticket saved = ticketRepository.save(ticket);
        return ticketMapper.toDTO(saved);
    }

    public List<TicketResponseDTO> findMyTickets(User user) {
        return ticketRepository.findByUser(user)
                .stream()
                .map(ticketMapper::toDTO)
                .toList();
    }

    public TicketResponseDTO findById(Long id, User user) {
        Ticket ticket = ticketRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));
        return ticketMapper.toDTO(ticket);
    }

    @Transactional
    public TicketResponseDTO cancel(Long id, User user) {

        // 1. Verificar que el ticket existe y pertenece al usuario
        Ticket ticket = ticketRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));

        // 2. Verificar que el ticket está activo
        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            throw new BusinessException(
                    "Solo puedes cancelar tickets activos");
        }

        // 3. Verificar que el evento no ha ocurrido
        if (LocalDateTime.now().isAfter(ticket.getTicketType().getEvent().getEventDate())) {
            throw new BusinessException(
                    "No puedes cancelar un ticket de un evento que ya ocurrió");
        }

        // 4. Cancelar el ticket
        ticket.setStatus(TicketStatus.CANCELLED);
        ticket.setCancelledAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        // 5. Devolver el cupo — con bloqueo pesimista
        TicketType ticketType = ticketRepository
                .findTicketTypeForUpdate(ticket.getTicketType().getId());
        ticketType.setAvailableCapacity(ticketType.getAvailableCapacity() + 1);
        ticketTypeRepository.save(ticketType);

        return ticketMapper.toDTO(ticket);
    }

    public List<TicketResponseDTO> findByEvent(Long eventId, User organizer) {
        return ticketRepository
                .findByEventIdAndStatus(eventId, TicketStatus.ACTIVE)
                .stream()
                .map(ticketMapper::toDTO)
                .toList();
    }

    // — Métodos privados auxiliares —

    private void checkAndMarkSoldOut(TicketType ticketType) {
        boolean allSoldOut = ticketType.getEvent().getTicketTypes()
                .stream()
                .allMatch(tt -> tt.getAvailableCapacity() == 0);

        if (allSoldOut) {
            ticketType.getEvent().setStatus(EventStatus.SOLD_OUT);
        }
    }
}