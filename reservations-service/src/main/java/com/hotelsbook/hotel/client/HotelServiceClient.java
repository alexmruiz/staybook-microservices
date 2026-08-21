package com.hotelsbook.hotel.client;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.hotelsbook.hotel.dto.HotelServiceDto;

@Service
public class HotelServiceClient {

    @Value("${microservice.services.url}")
    private String servicesUrl;

    @Autowired
    private WebClient webClient;

    /**
     * Llama al microservicio de servicios de hotel para obtener los servicios de los hoteles dados sus IDs.
     * @param hotelIds
     * @return
     */
    public List<HotelServiceDto> getHotelServices(List<Long> hotelIds) {
        String hotelIdsParam = hotelIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String url = servicesUrl + "/" + hotelIdsParam;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(HotelServiceDto.class)
                .collectList()
                .block(); 
    }
}
