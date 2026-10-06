package com.hotelsbook.services.com_hotelsbook_services.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotelsbook.services.com_hotelsbook_services.client.ReviewsClient;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.ReviewResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.ReviewsSummaryResponseDto;

@ExtendWith(MockitoExtension.class)
class ExternalReviewServiceTest {

    @Mock
    private ReviewsClient reviewsClient;

    @InjectMocks
    private ExternalReviewService service;

    @Test
    void getReviews_WhenClientReturnsSummary_ReturnsSummary() {
        Long hotelId = 42L;
        ReviewsSummaryResponseDto expected = new ReviewsSummaryResponseDto(
                List.of(new ReviewResponseDto("Muy bien", 4.5)),
                4.5);
        when(reviewsClient.getSummaryByHotelId(hotelId)).thenReturn(expected);

        ReviewsSummaryResponseDto result = service.getReviews(hotelId);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(reviewsClient).getSummaryByHotelId(hotelId);
    }

    @Test
    void reviewsFallback_WhenCalled_ReturnsEmptySummary() {
        Long hotelId = 42L;
        Exception exception = new RuntimeException("Reviews service unavailable");

        ReviewsSummaryResponseDto result = service.reviewsFallback(hotelId, exception);

        assertNotNull(result);
        assertTrue(result.reviews().isEmpty());
        assertEquals(Double.valueOf(0.0), result.averageQualification());
        verifyNoInteractions(reviewsClient);
    }
}
