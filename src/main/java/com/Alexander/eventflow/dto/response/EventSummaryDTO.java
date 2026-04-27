package com.Alexander.eventflow.dto.response;

import com.Alexander.eventflow.model.enums.EventStatus;

import java.time.LocalDateTime;

public record EventSummaryDTO(
        Long id,
        String title,
        String location,
        LocalDateTime eventDate,
        EventStatus status,
        String category,
        String organizerName,
        int totalTicketTypes
) {}