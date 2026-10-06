package com.hotelsbook.services.com_hotelsbook_services.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hotelsbook.services.com_hotelsbook_services.client.ReviewsClient;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.ReviewsSummaryResponseDto;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

public class ExternalReviewService {

    private static final Logger log = LoggerFactory.getLogger(ExternalReviewService.class);

    private final ReviewsClient reviewsClient;

    /** Creates the service with its reviews client. */
    public ExternalReviewService(ReviewsClient reviewsClient) {
        this.reviewsClient = reviewsClient;
    }

    /**
     * Obtiene las reseñas y su calificación media para un hotel.
     *
     * @param hotelId identificador del hotel
     * @return resumen de reseñas
     */
    @CircuitBreaker(name = "reviewsService", fallbackMethod = "reviewsFallback")
    public ReviewsSummaryResponseDto getReviews(Long hotelId) {
        return reviewsClient.getSummaryByHotelId(hotelId);
    }

    /**
     * Devuelve un resumen vacío cuando falla el servicio de reseñas.
     *
     * @param hotelId identificador del hotel
     * @param exception error recibido del servicio remoto
     * @return resumen sin reseñas y con media cero
     */
    public ReviewsSummaryResponseDto reviewsFallback(Long hotelId, Exception exception) {
        log.warn("Fallback activado para reseñas del hotel {}: {}", hotelId, exception.getMessage());

        return new ReviewsSummaryResponseDto(List.of(), 0.0);
    }
}