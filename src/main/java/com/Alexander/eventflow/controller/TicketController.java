package com.Alexander.eventflow.controller;

import com.Alexander.eventflow.dto.request.PurchaseTicketRequest;
import com.Alexander.eventflow.dto.response.TicketResponseDTO;
import com.Alexander.eventflow.model.User;
import com.Alexander.eventflow.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/purchase")
    public ResponseEntity<TicketResponseDTO> purchase(
            @Valid @RequestBody PurchaseTicketRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ticketService.purchase(request, user));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TicketResponseDTO>> findMyTickets(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ticketService.findMyTickets(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ticketService.findById(id, user));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TicketResponseDTO> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ticketService.cancel(id, user));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<TicketResponseDTO>> findByEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ticketService.findByEvent(eventId, user));
    }
}