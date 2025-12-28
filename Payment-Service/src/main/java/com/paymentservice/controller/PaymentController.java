package com.paymentservice.controller;

import com.paymentservice.dto.PaymentRequest;
import com.paymentservice.model.Payment;
import com.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> process(@RequestBody PaymentRequest request) {
        try {
            Payment payment = paymentService.processPayment(request);
            HttpStatus status = payment.getStatus().name().equals("SUCCESS") ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(payment);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAll() {
        return ResponseEntity.ok(paymentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(paymentService.getById(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<Payment>> getByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(paymentService.getByReservation(reservationId));
    }
}
