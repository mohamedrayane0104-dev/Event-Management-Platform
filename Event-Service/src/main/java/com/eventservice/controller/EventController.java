package com.eventservice.controller;

import com.eventservice.model.Event;
import com.eventservice.model.Participant;
import com.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event created = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEvent(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllActiveEvents() {
        return ResponseEntity.ok(eventService.getAllActiveEvents());
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<Event>> getEventsByOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(eventService.getEventsByOrganizer(organizerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails) {
        try {
            Event updated = eventService.updateEvent(id, eventDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/{eventId}/register")
    public ResponseEntity<?> registerParticipant(
            @PathVariable Long eventId,
            @RequestParam Long userId,
            @RequestParam Integer ticketsRequested) {
        try {
            Participant participant = eventService.registerParticipant(eventId, userId, ticketsRequested);
            return ResponseEntity.status(HttpStatus.CREATED).body(participant);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<Participant>> getEventParticipants(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventParticipants(eventId));
    }

    @GetMapping("/user/{userId}/registrations")
    public ResponseEntity<List<Participant>> getUserRegistrations(@PathVariable Long userId) {
        return ResponseEntity.ok(eventService.getUserRegistrations(userId));
    }

    @DeleteMapping("/participants/{participantId}")
    public ResponseEntity<?> cancelRegistration(@PathVariable Long participantId) {
        try {
            eventService.cancelRegistration(participantId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}