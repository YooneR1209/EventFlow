package com.Alexander.eventflow.dto.response;

import com.Alexander.eventflow.model.enums.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketResponseDTO(
        Long id,
        String code,
        TicketStatus status,
        LocalDateTime purchasedAt,
        LocalDateTime cancelledAt,

        // Datos del evento
        Long eventId,
        String eventTitle,
        String eventLocation,
        LocalDateTime eventDate,

        // Datos del tipo de ticket
        String ticketTypeName,
        BigDecimal ticketTypePrice,

        // Datos del usuario
        String userName,
        String userEmail
) {}