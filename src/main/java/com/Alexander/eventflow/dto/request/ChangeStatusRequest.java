package com.Alexander.eventflow.dto.request;

import com.Alexander.eventflow.model.enums.EventStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(

        @NotNull(message = "El estado es obligatorio")
        EventStatus status
) {}