package com.reservationservice.dto;

import lombok.Data;

@Data
public class CreateReservationRequest {
    private Long eventId;
    private Long userId;
    private Integer quantity;
}
