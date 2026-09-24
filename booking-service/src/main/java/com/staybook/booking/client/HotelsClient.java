package com.staybook.booking.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.staybook.booking.dto.response.HotelSummaryDto;

@HttpExchange("/api/hotels")
public interface HotelsClient {
    
    @GetExchange("/{id}")
    HotelSummaryDto getHotelById(@PathVariable("id") Long id);
}
