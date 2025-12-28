package com.reservationservice;

import com.reservationservice.dto.CreateReservationRequest;
import com.reservationservice.model.Reservation;
import com.reservationservice.service.ReservationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ReservationIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setupServer() {
        SecurityContextHolder.clearContext();
        this.mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
    }

    @AfterEach
    void after() {
        mockServer.verify();
        SecurityContextHolder.clearContext();
    }

    @Test
    void createReservation_callsEventService_andPersistsReservation() {
        SecurityContextHolder.getContext().setAuthentication(authWithUserId(5L, "user1"));

        mockServer.expect(ExpectedCount.once(),
                        MockRestRequestMatchers.requestTo("http://event-service:8082/api/events/1"))
                .andRespond(MockRestResponseCreators.withSuccess("{\"id\":1,\"ticketPrice\":50,\"availableTickets\":100,\"active\":true}", MediaType.APPLICATION_JSON));

        mockServer.expect(ExpectedCount.once(),
                        MockRestRequestMatchers.requestTo(org.hamcrest.Matchers.containsString("/api/events/1/register")))
                .andRespond(MockRestResponseCreators.withSuccess("{\"id\":10,\"userId\":5,\"ticketsBooked\":2,\"event\":{\"id\":1}}", MediaType.APPLICATION_JSON));

        CreateReservationRequest request = new CreateReservationRequest();
        request.setEventId(1L);
        request.setUserId(5L);
        request.setQuantity(2);

        Reservation reservation = reservationService.createReservation(request);

        assertThat(reservation.getStatus().name()).isEqualTo("PENDING_PAYMENT");
        assertThat(reservation.getParticipantId()).isEqualTo(10L);
        assertThat(reservation.getTotalAmount()).isEqualTo(100.0);
    }

    @Test
    void createReservation_rejectsDifferentUserIdThanToken() {
        SecurityContextHolder.getContext().setAuthentication(authWithUserId(6L, "user2"));

        CreateReservationRequest request = new CreateReservationRequest();
        request.setEventId(1L);
        request.setUserId(5L);
        request.setQuantity(1);

        try {
            reservationService.createReservation(request);
        } catch (IllegalStateException ex) {
            assertThat(ex.getMessage()).contains("not allowed");
        }
    }

    private UsernamePasswordAuthenticationToken authWithUserId(Long userId, String username) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, null, java.util.Collections.emptyList());
        authenticationToken.setDetails(userId);
        return authenticationToken;
    }
}
