package com.eventservice.service;

import com.eventservice.model.Event;
import com.eventservice.model.Participant;
import com.eventservice.repository.EventRepository;
import com.eventservice.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    public Event createEvent(Event event) {
        event.setAvailableTickets(event.getTotalTickets());
        return eventRepository.save(event);
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> getAllActiveEvents() {
        return eventRepository.findByActive(true);
    }

    public List<Event> getEventsByOrganizer(Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }

    public Event updateEvent(Long id, Event eventDetails) {
        return eventRepository.findById(id).map(event -> {
            event.setTitle(eventDetails.getTitle());
            event.setDescription(eventDetails.getDescription());
            event.setEventDate(eventDetails.getEventDate());
            event.setLocation(eventDetails.getLocation());
            event.setTicketPrice(eventDetails.getTicketPrice());
            return eventRepository.save(event);
        }).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Participant registerParticipant(Long eventId, Long userId, Integer ticketsRequested) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Optional<Participant> existing = participantRepository.findByEventIdAndUserId(eventId, userId);
        if (existing.isPresent()) {
            throw new RuntimeException("User already registered for this event");
        }

        if (ticketsRequested == null || ticketsRequested < 1 || ticketsRequested > 4) {
            throw new RuntimeException("You can only reserve between 1 and 4 tickets");
        }

        if (event.getAvailableTickets() < ticketsRequested) {
            throw new RuntimeException("Not enough tickets available");
        }

        Participant participant = new Participant();
        participant.setEvent(event);
        participant.setUserId(userId);
        participant.setTicketsBooked(ticketsRequested);
        participant.setStatus("REGISTERED");

        event.setAvailableTickets(event.getAvailableTickets() - ticketsRequested);
        eventRepository.save(event);

        return participantRepository.save(participant);
    }

    public List<Participant> getEventParticipants(Long eventId) {
        return participantRepository.findByEventId(eventId);
    }

    public List<Participant> getUserRegistrations(Long userId) {
        return participantRepository.findByUserId(userId);
    }

    public void cancelRegistration(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        Event event = participant.getEvent();
        event.setAvailableTickets(event.getAvailableTickets() + participant.getTicketsBooked());
        eventRepository.save(event);

        participantRepository.deleteById(participantId);
    }
}
