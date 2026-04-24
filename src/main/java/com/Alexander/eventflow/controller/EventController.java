package com.Alexander.eventflow.controller;

import com.Alexander.eventflow.dto.request.ChangeStatusRequest;
import com.Alexander.eventflow.dto.request.CreateEventRequest;
import com.Alexander.eventflow.dto.request.UpdateEventRequest;
import com.Alexander.eventflow.dto.response.EventDetailDTO;
import com.Alexander.eventflow.dto.response.EventSummaryDTO;
import com.Alexander.eventflow.model.User;
import com.Alexander.eventflow.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // — Endpoints públicos —

    @GetMapping
    public ResponseEntity<Page<EventSummaryDTO>> findAll(
            @PageableDefault(size = 10, sort = "eventDate", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(eventService.search(search, pageable));
        }
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(eventService.findByCategory(category, pageable));
        }
        return ResponseEntity.ok(eventService.findAllPublished(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDetailDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    // — Endpoints del organizador —

    @GetMapping("/my")
    public ResponseEntity<List<EventSummaryDTO>> findMyEvents(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(eventService.findMyEvents(user));
    }

    @PostMapping
    public ResponseEntity<EventDetailDTO> create(
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.create(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDetailDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(eventService.update(id, request, user));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EventDetailDTO> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeStatusRequest request,
            @AuthenticationPrincipal User user) {
            System.out.println("AQUIII " + user.getUsername() + " " + user.getAuthorities().toString() );
        return ResponseEntity.ok(eventService.changeStatus(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        eventService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}