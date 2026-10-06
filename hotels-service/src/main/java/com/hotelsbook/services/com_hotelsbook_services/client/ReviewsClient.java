package com.hotelsbook.services.com_hotelsbook_services.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.hotelsbook.services.com_hotelsbook_services.dto.response.ReviewsSummaryResponseDto;

@FeignClient(name = "reviews-service", url = "${reviews.service.url}")
public interface ReviewsClient {

    @GetMapping("/api/reviews/hotel/{hotelId}/summary")
    ReviewsSummaryResponseDto getSummaryByHotelId(
        @PathVariable ("hotelId") Long hotelId);
    
}
