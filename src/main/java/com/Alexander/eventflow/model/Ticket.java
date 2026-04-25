package com.Alexander.eventflow.model;

import com.Alexander.eventflow.model.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime purchasedAt;

    private LocalDateTime cancelledAt;

    @Version
    private Long version;

    // Relación con User — muchos tickets pertenecen a un usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Relación con TicketType — muchos tickets son de un tipo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketType ticketType;

    @PrePersist
    protected void onCreate() {
        this.code = UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 12);
        this.status = TicketStatus.ACTIVE;
        this.purchasedAt = LocalDateTime.now();
    }
}