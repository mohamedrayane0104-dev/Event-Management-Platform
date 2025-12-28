package com.paymentservice.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long reservationId;
    private String paymentMethod;
    private String cardNumber;
    private boolean simulateFailure;
}
