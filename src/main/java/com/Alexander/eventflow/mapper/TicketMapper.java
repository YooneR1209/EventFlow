package com.Alexander.eventflow.mapper;

import com.Alexander.eventflow.dto.response.TicketResponseDTO;
import com.Alexander.eventflow.model.Ticket;
import com.Alexander.eventflow.model.TicketType;
import com.Alexander.eventflow.model.User;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketResponseDTO toDTO(Ticket ticket) {
        TicketType ticketType = ticket.getTicketType();
        User user = ticket.getUser();

        return new TicketResponseDTO(
                ticket.getId(),
                ticket.getCode(),
                ticket.getStatus(),
                ticket.getPurchasedAt(),
                ticket.getCancelledAt(),

                // Datos del evento — navegando la relación
                ticketType.getEvent().getId(),
                ticketType.getEvent().getTitle(),
                ticketType.getEvent().getLocation(),
                ticketType.getEvent().getEventDate(),

                // Datos del tipo de ticket
                ticketType.getName(),
                ticketType.getPrice(),

                // Datos del usuario
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail()
        );
    }
}