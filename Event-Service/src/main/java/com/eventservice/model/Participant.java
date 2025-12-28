package com.eventservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "participants", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"event_id", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnoreProperties({"participants"})
    private Event event;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer ticketsBooked;

    @Column(nullable = false)
    private String status;

    private LocalDateTime registeredAt;

    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }
}