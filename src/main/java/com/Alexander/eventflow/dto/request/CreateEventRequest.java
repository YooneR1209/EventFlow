package com.Alexander.eventflow.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public record CreateEventRequest(

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 100, message = "El título no puede superar 100 caracteres")
        String title,

        String description,

        @NotBlank(message = "La ubicación es obligatoria")
        String location,

        @NotNull(message = "La fecha del evento es obligatoria")
        @Future(message = "La fecha del evento debe ser futura")
        LocalDateTime eventDate,

        @NotNull(message = "La fecha límite de registro es obligatoria")
        @Future(message = "La fecha límite debe ser futura")
        LocalDateTime registrationDeadline,

        @NotBlank(message = "La categoría es obligatoria")
        String category,

        @NotEmpty(message = "Debe tener al menos un tipo de ticket")
        @Valid
        List<TicketTypeRequest> ticketTypes
) {
    public record TicketTypeRequest(

            @NotBlank(message = "El nombre del tipo de ticket es obligatorio")
            String name,

            String description,

            @NotNull(message = "El precio es obligatorio")
            @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
            java.math.BigDecimal price,

            @NotNull(message = "La capacidad es obligatoria")
            @Min(value = 1, message = "La capacidad debe ser al menos 1")
            Integer totalCapacity
    ) {}
}