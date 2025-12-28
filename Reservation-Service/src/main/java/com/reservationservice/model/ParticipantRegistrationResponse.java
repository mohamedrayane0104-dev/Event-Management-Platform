package com.reservationservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParticipantRegistrationResponse {
    private Long id;
    private Long userId;
    private Integer ticketsBooked;
    private EventSummary event;
}
