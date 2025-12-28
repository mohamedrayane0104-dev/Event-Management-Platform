package com.reservationservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventSummary {
    private Long id;
    private String title;
    private Integer availableTickets;
    private Double ticketPrice;
    private Boolean active;
}
