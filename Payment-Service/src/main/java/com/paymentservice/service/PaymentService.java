package com.paymentservice.service;

import com.paymentservice.dto.PaymentRequest;
import com.paymentservice.model.Payment;
import com.paymentservice.model.PaymentStatus;
import com.paymentservice.model.ReservationSummary;
import com.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    @Value("${reservation.service.url:http://localhost:8083}")
    private String reservationServiceUrl;

    public Payment processPayment(PaymentRequest request) {
        validateRequest(request);
        ReservationSummary reservation = fetchReservation(request.getReservationId());

        if (!"PENDING_PAYMENT".equalsIgnoreCase(reservation.getStatus())) {
            throw new IllegalStateException("Reservation is not awaiting payment");
        }

        if (reservation.getTotalAmount() == null) {
            throw new IllegalStateException("Reservation total amount is missing");
        }
        double amount = reservation.getTotalAmount();
        boolean isFailure = request.isSimulateFailure() || (request.getCardNumber() != null && request.getCardNumber().endsWith("0000"));

        Payment payment = new Payment();
        payment.setReservationId(reservation.getId());
        payment.setAmount(amount);
        payment.setMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CARD");
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setStatus(isFailure ? PaymentStatus.FAILED : PaymentStatus.SUCCESS);

        Payment saved = paymentRepository.save(payment);

        if (!isFailure) {
            confirmReservation(reservation.getId());
        }

        return saved;
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public Payment getById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }

    public List<Payment> getByReservation(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId);
    }

    private void validateRequest(PaymentRequest request) {
        if (request.getReservationId() == null) {
            throw new IllegalArgumentException("Reservation ID is required");
        }
    }

    @CircuitBreaker(name = "reservationService")
    private ReservationSummary fetchReservation(Long reservationId) {
        try {
            String url = reservationServiceUrl + "/api/reservations/" + reservationId;
            ReservationSummary reservation = restTemplate.getForObject(url, ReservationSummary.class);
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found");
            }
            return reservation;
        } catch (HttpClientErrorException.NotFound ex) {
            throw new IllegalArgumentException("Reservation not found");
        } catch (HttpClientErrorException ex) {
            throw new IllegalStateException("Unable to reach reservation service: " + ex.getStatusText());
        }
    }

    @CircuitBreaker(name = "reservationService")
    private void confirmReservation(Long reservationId) {
        try {
            String url = reservationServiceUrl + "/api/reservations/" + reservationId + "/confirm";
            restTemplate.postForEntity(url, null, ReservationSummary.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() != HttpStatus.OK) {
                throw new IllegalStateException("Payment succeeded but reservation confirmation failed: " + ex.getResponseBodyAsString());
            }
        }
    }
}
