package com.Alexander.eventflow.mapper;

import com.Alexander.eventflow.dto.request.CreateEventRequest;
import com.Alexander.eventflow.dto.response.EventDetailDTO;
import com.Alexander.eventflow.dto.response.EventSummaryDTO;
import com.Alexander.eventflow.model.Event;
import com.Alexander.eventflow.model.TicketType;
import com.Alexander.eventflow.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventMapper {

    // CreateEventRequest → Event
    public Event toEntity(CreateEventRequest request, User organizer) {
        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .location(request.location())
                .eventDate(request.eventDate())
                .registrationDeadline(request.registrationDeadline())
                .category(request.category())
                .organizer(organizer)
                .build();

        // Mapear cada TicketTypeRequest → TicketType
        List<TicketType> ticketTypes = request.ticketTypes().stream()
                .map(ttRequest -> TicketType.builder()
                        .name(ttRequest.name())
                        .description(ttRequest.description())
                        .price(ttRequest.price())
                        .totalCapacity(ttRequest.totalCapacity())
                        .availableCapacity(ttRequest.totalCapacity())
                        .event(event)
                        .build()
                )
                .toList();

        event.getTicketTypes().addAll(ticketTypes);
        return event;
    }

    // Event → EventSummaryDTO
    public EventSummaryDTO toSummaryDTO(Event event) {
        return new EventSummaryDTO(
                event.getId(),
                event.getTitle(),
                event.getLocation(),
                event.getEventDate(),
                event.getStatus(),
                event.getCategory(),
                event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName(),
                event.getTicketTypes().size()
        );
    }

    // Event → EventDetailDTO
    public EventDetailDTO toDetailDTO(Event event) {
        List<EventDetailDTO.TicketTypeDTO> ticketTypeDTOs = event.getTicketTypes()
                .stream()
                .map(this::toTicketTypeDTO)
                .toList();

        return new EventDetailDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getEventDate(),
                event.getRegistrationDeadline(),
                event.getStatus(),
                event.getCategory(),
                event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName(),
                event.getOrganizer().getEmail(),
                event.getCreatedAt(),
                ticketTypeDTOs
        );
    }

    // TicketType → TicketTypeDTO
    private EventDetailDTO.TicketTypeDTO toTicketTypeDTO(TicketType ticketType) {
        return new EventDetailDTO.TicketTypeDTO(
                ticketType.getId(),
                ticketType.getName(),
                ticketType.getDescription(),
                ticketType.getPrice(),
                ticketType.getTotalCapacity(),
                ticketType.getAvailableCapacity()
        );
    }
}