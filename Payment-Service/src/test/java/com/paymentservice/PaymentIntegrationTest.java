package com.paymentservice;

import com.paymentservice.dto.PaymentRequest;
import com.paymentservice.model.Payment;
import com.paymentservice.model.PaymentStatus;
import com.paymentservice.repository.PaymentRepository;
import com.paymentservice.service.PaymentService;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class PaymentIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setupServer() {
        this.mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
    }

    @AfterEach
    void verifyServer() {
        mockServer.verify();
    }

    @Test
    void processPayment_succeeds_andConfirmsReservation() {
        mockServer.expect(ExpectedCount.once(),
                        MockRestRequestMatchers.requestTo("http://reservation-service:8083/api/reservations/1"))
                .andRespond(MockRestResponseCreators.withSuccess("{\"id\":1,\"totalAmount\":75.0,\"status\":\"PENDING_PAYMENT\"}", MediaType.APPLICATION_JSON));

        mockServer.expect(ExpectedCount.once(),
                        MockRestRequestMatchers.requestTo("http://reservation-service:8083/api/reservations/1/confirm"))
                .andRespond(MockRestResponseCreators.withSuccess("{\"id\":1,\"status\":\"CONFIRMED\"}", MediaType.APPLICATION_JSON));

        PaymentRequest request = new PaymentRequest();
        request.setReservationId(1L);
        request.setPaymentMethod("CARD");

        Payment payment = paymentService.processPayment(request);

        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getAmount()).isEqualTo(75.0);

        Payment persisted = paymentRepository.findById(payment.getId()).orElseThrow();
        assertThat(persisted.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }
}
