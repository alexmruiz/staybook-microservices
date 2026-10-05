package com.staybook.booking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.staybook.booking.client.HotelsClient;
import com.staybook.booking.client.ReviewsClient;
import com.staybook.booking.dto.response.HotelSummaryDto;
import com.staybook.booking.dto.response.ReviewSummaryDto;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class ExternalHotelService {

    private final HotelsClient hotelsClient;
    private final ReviewsClient reviewsClient;

    public ExternalHotelService(HotelsClient hotelsClient, ReviewsClient reviewsClient) {
        this.hotelsClient = hotelsClient;
        this.reviewsClient = reviewsClient;
    }

    @CircuitBreaker(name = "hotelsServiceCB", fallbackMethod = "fallbackGetHotel")
    public HotelSummaryDto getHotel(Long hotelId) {
        return hotelsClient.getHotelById(hotelId);
    }

    private HotelSummaryDto fallbackGetHotel(Long hotelId, Throwable exception) {

        return null;
    }

    @CircuitBreaker(name = "reviewsServiceCB", fallbackMethod = "fallbackGetReviews")
    public List<ReviewSummaryDto> getReviews(Long hotelId) {
        return reviewsClient.getReviewsByHotelId(hotelId);
    }

    private List<ReviewSummaryDto> fallbackGetReviews(Long hotelId, Throwable exception) {

        return List.of();
    }
}
