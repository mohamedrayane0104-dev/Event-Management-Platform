package com.reservationservice.service;

import com.reservationservice.dto.CreateReservationRequest;
import com.reservationservice.model.EventSummary;
import com.reservationservice.model.ParticipantRegistrationResponse;
import com.reservationservice.model.Reservation;
import com.reservationservice.model.ReservationStatus;
import com.reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestTemplate restTemplate;

    @Value("${event.service.url:http://localhost:8082}")
    private String eventServiceUrl;

    public Reservation createReservation(CreateReservationRequest request) {
        validateRequest(request);
        Long authenticatedUserId = getAuthenticatedUserId();
        if (!authenticatedUserId.equals(request.getUserId())) {
            throw new IllegalStateException("User is not allowed to create a reservation for another account");
        }

        EventSummary event = fetchEventDetails(request.getEventId());
        if (Boolean.FALSE.equals(event.getActive())) {
            throw new IllegalStateException("Event is not active");
        }
        if (event.getAvailableTickets() == null || event.getAvailableTickets() < request.getQuantity()) {
            throw new IllegalStateException("Not enough tickets available");
        }
        if (event.getTicketPrice() == null) {
            throw new IllegalStateException("Event ticket price is not set");
        }

        ParticipantRegistrationResponse participant = registerParticipant(event.getId(), request.getUserId(), request.getQuantity());

        Reservation reservation = new Reservation();
        reservation.setEventId(event.getId());
        reservation.setUserId(request.getUserId());
        reservation.setQuantity(request.getQuantity());
        reservation.setTotalAmount(event.getTicketPrice() * request.getQuantity());
        reservation.setStatus(ReservationStatus.PENDING_PAYMENT);
        reservation.setParticipantId(participant != null ? participant.getId() : null);

        return reservationRepository.save(reservation);
    }

    public Reservation confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation already cancelled");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        return reservationRepository.save(reservation);
    }

    public Reservation cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot cancel a confirmed reservation");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return reservation;
        }

        if (reservation.getParticipantId() != null) {
            String url = eventServiceUrl + "/api/events/participants/" + reservation.getParticipantId();
            try {
                restTemplate.delete(url);
            } catch (HttpClientErrorException.NotFound ignored) {
                // Participant already removed upstream; proceed with local cancellation.
            }
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservationRepository.save(reservation);
    }

    public Optional<Reservation> getReservation(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getByEvent(Long eventId) {
        return reservationRepository.findByEventId(eventId);
    }

    private void validateRequest(CreateReservationRequest request) {
        if (request.getEventId() == null || request.getUserId() == null || request.getQuantity() == null) {
            throw new IllegalArgumentException("Event, user and quantity are required");
        }
        if (request.getQuantity() < 1 || request.getQuantity() > 4) {
            throw new IllegalArgumentException("You can only reserve between 1 and 4 tickets");
        }
    }

    @CircuitBreaker(name = "eventService")
    private EventSummary fetchEventDetails(Long eventId) {
        try {
            String url = eventServiceUrl + "/api/events/" + eventId;
            EventSummary event = restTemplate.getForObject(url, EventSummary.class);
            if (event == null) {
                throw new IllegalArgumentException("Event not found");
            }
            return event;
        } catch (HttpClientErrorException.NotFound ex) {
            throw new IllegalArgumentException("Event not found");
        } catch (HttpClientErrorException ex) {
            throw new IllegalStateException("Unable to reach event service: " + ex.getStatusText());
        }
    }

    @CircuitBreaker(name = "eventService")
    private ParticipantRegistrationResponse registerParticipant(Long eventId, Long userId, Integer ticketsRequested) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(eventServiceUrl + "/api/events/{eventId}/register")
                .queryParam("userId", userId)
                .queryParam("ticketsRequested", ticketsRequested)
                .buildAndExpand(eventId)
                .toUri();

        try {
            return restTemplate.postForObject(uri, null, ParticipantRegistrationResponse.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST || ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new IllegalStateException(ex.getResponseBodyAsString());
            }
            throw new IllegalStateException("Failed to reserve tickets on event service: " + ex.getStatusText());
        }
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getDetails() == null) {
            throw new IllegalStateException("Unauthorized");
        }
        Object details = authentication.getDetails();
        if (details instanceof Long) {
            return (Long) details;
        }
        if (details instanceof Integer) {
            return ((Integer) details).longValue();
        }
        throw new IllegalStateException("Unauthorized");
    }
}
