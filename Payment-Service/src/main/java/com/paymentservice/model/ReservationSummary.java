package com.paymentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservationSummary {
    private Long id;
    private Long eventId;
    private Long userId;
    private Integer quantity;
    private Double totalAmount;
    private String status;
}
