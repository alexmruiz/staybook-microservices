package com.staybook.booking.client;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.staybook.booking.dto.response.ReviewSummaryDto;

@HttpExchange("/api/reviews")
public interface ReviewsClient {
    
    @GetExchange ("/hotel/{hotelId}")
    List<ReviewSummaryDto> getReviewsByHotelId(@PathVariable("hotelId") Long hotelId);

}
