package com.hotelsbook.services.com_hotelsbook_services.client;

import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.CountriesNowRequest;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.CountriesNowResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@HttpExchange 
public interface CountriesNowClient {

    @PostExchange("/countries/cities")
    CountriesNowResponse getCitiesByCountry(@RequestBody CountriesNowRequest request);
    
}
