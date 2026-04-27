package com.Alexander.eventflow.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateEventRequest(

        @Size(max = 100, message = "El título no puede superar 100 caracteres")
        String title,

        String description,

        String location,

        @Future(message = "La fecha del evento debe ser futura")
        LocalDateTime eventDate,

        @Future(message = "La fecha límite debe ser futura")
        LocalDateTime registrationDeadline,

        String category
) {}