package com.staybook.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.staybook.booking.client.HotelsClient;
import com.staybook.booking.client.ReviewsClient;
import com.staybook.booking.dto.response.HotelSummaryDto;
import com.staybook.booking.dto.response.ReviewSummaryDto;

@ExtendWith(MockitoExtension.class)
class ExternalHotelServiceTest {

    @Mock
    private HotelsClient hotelsClient;

    @Mock
    private ReviewsClient reviewsClient;

    @InjectMocks
    private ExternalHotelService service;

    @Test
    void getHotel_shouldReturnHotelFromHotelsClient() {
        Long hotelId = 1L;
        HotelSummaryDto expectedHotel = new HotelSummaryDto(hotelId, "Hotel X", 4, List.of());
        when(hotelsClient.getHotelById(hotelId)).thenReturn(expectedHotel);

        HotelSummaryDto result = service.getHotel(hotelId);

        assertSame(expectedHotel, result);
        verify(hotelsClient).getHotelById(hotelId);
    }

    @Test
    void getReviews_shouldReturnReviewsFromReviewsClient() {
        Long hotelId = 1L;
        List<ReviewSummaryDto> expectedReviews = List.of(
                new ReviewSummaryDto(hotelId, 2L, 4.5, "Nice"));
        when(reviewsClient.getReviewsByHotelId(hotelId)).thenReturn(expectedReviews);

        List<ReviewSummaryDto> result = service.getReviews(hotelId);

        assertSame(expectedReviews, result);
        verify(reviewsClient).getReviewsByHotelId(hotelId);
    }

    @Test
    void fallbackGetHotel_shouldReturnNull() {
        Long hotelId = 1L;
        RuntimeException exception = new RuntimeException("hotels-service unavailable");

        HotelSummaryDto result = ReflectionTestUtils.invokeMethod(
                service, "fallbackGetHotel", hotelId, exception);

        assertNull(result);
    }

    @Test
    void fallbackGetReviews_shouldReturnEmptyList() {
        Long hotelId = 1L;
        RuntimeException exception = new RuntimeException("reviews-service unavailable");

        List<ReviewSummaryDto> result = ReflectionTestUtils.invokeMethod(
                service, "fallbackGetReviews", hotelId, exception);

        assertEquals(List.of(), result);
    }
}
