package com.staybook.booking.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.staybook.booking.dto.response.ReviewSumaryDto;

@HttpExchange("/api/reviews")
public interface ReviewsClient {
    
    @GetExchange ("/hotel/{hotelId}")
    ReviewSumaryDto getReviewsByHotelId(@PathVariable("hotelId") Long hotelId);

}
