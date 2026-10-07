package com.hotelsbook.services.com_hotelsbook_services.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.hotelsbook.services.com_hotelsbook_services.client.ReviewsClient;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.ReviewsSummaryResponseDto;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@SpringBootTest(
        classes = ExternalReviewServiceCircuitBreakerTest.TestApplication.class,
        properties = {
                "spring.main.web-application-type=none",
                "resilience4j.circuitbreaker.instances.reviewsService.sliding-window-type=COUNT_BASED",
                "resilience4j.circuitbreaker.instances.reviewsService.sliding-window-size=2",
                "resilience4j.circuitbreaker.instances.reviewsService.minimum-number-of-calls=2",
                "resilience4j.circuitbreaker.instances.reviewsService.failure-rate-threshold=50",
                "resilience4j.circuitbreaker.instances.reviewsService.wait-duration-in-open-state=60s",
                "resilience4j.circuitbreaker.instances.reviewsService.permitted-number-of-calls-in-half-open-state=1"
        })
class ExternalReviewServiceCircuitBreakerTest {

    @MockitoBean
    private ReviewsClient reviewsClient;

    @Autowired
    private ExternalReviewService externalReviewService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    void getReviews_WhenFailuresReachThreshold_OpensCircuitAndUsesFallback() {
        Long hotelId = 42L;
        when(reviewsClient.getSummaryByHotelId(hotelId))
                .thenThrow(new IllegalStateException("Reviews service unavailable"));

        assertTrue(AopUtils.isAopProxy(externalReviewService));
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("reviewsService");
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());

        ReviewsSummaryResponseDto firstResult = externalReviewService.getReviews(hotelId);
        ReviewsSummaryResponseDto secondResult = externalReviewService.getReviews(hotelId);

        assertTrue(firstResult.reviews().isEmpty());
        assertEquals(Double.valueOf(0.0), firstResult.averageQualification());
        assertTrue(secondResult.reviews().isEmpty());
        assertEquals(Double.valueOf(0.0), secondResult.averageQualification());
        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());

        ReviewsSummaryResponseDto openCircuitResult = externalReviewService.getReviews(hotelId);

        assertTrue(openCircuitResult.reviews().isEmpty());
        assertEquals(Double.valueOf(0.0), openCircuitResult.averageQualification());
        verify(reviewsClient, times(2)).getSummaryByHotelId(hotelId);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
    })
    @Import(ExternalReviewService.class)
    static class TestApplication {
    }
}
