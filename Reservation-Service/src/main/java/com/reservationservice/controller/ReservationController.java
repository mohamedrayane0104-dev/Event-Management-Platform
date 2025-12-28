package com.reservationservice.controller;

import com.reservationservice.dto.CreateReservationRequest;
import com.reservationservice.model.Reservation;
import com.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody CreateReservationRequest request) {
        try {
            Reservation reservation = reservationService.createReservation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            if ("Unauthorized".equalsIgnoreCase(ex.getMessage())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAll() {
        return ResponseEntity.ok(reservationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return reservationService.getReservation(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservation not found"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getByUser(userId));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Reservation>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(reservationService.getByEvent(eventId));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        try {
            Reservation reservation = reservationService.cancelReservation(id);
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirm(@PathVariable Long id) {
        try {
            Reservation reservation = reservationService.confirmReservation(id);
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
