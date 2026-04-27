package com.Alexander.eventflow.dto.response;

import com.Alexander.eventflow.model.enums.EventStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record EventDetailDTO(
        Long id,
        String title,
        String description,
        String location,
        LocalDateTime eventDate,
        LocalDateTime registrationDeadline,
        EventStatus status,
        String category,
        String organizerName,
        String organizerEmail,
        LocalDateTime createdAt,
        List<TicketTypeDTO> ticketTypes
) {
    public record TicketTypeDTO(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Integer totalCapacity,
            Integer availableCapacity
    ) {}
}