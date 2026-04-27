package com.Alexander.eventflow.dto.request;

import jakarta.validation.constraints.NotNull;

public record PurchaseTicketRequest(

        @NotNull(message = "El id del tipo de ticket es obligatorio")
        Long ticketTypeId
) {}