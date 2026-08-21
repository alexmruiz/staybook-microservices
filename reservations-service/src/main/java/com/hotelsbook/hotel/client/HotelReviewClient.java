package com.hotelsbook.hotel.client;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.hotelsbook.hotel.dto.HotelReviewDto;

@Service
public class HotelReviewClient {

    @Value("${microservice.reviews.url}")
    private String reviewsUrl;

    @Autowired
    private WebClient webClient;

    /**
     * Llama al microservicio de reviews de hotel para obtener las reviews de los hoteles dados sus IDs.
     * @param hotelIds
     * @return
     */
    public List<HotelReviewDto> getHotelReviews(List<Long> hotelIds) {
        String hotelIdsParam = hotelIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String url = reviewsUrl + "/" + hotelIdsParam;
        return webClient.get()//RequestHeadersUriSpec
            .uri(url)
            .retrieve() //ResponseSpec
            .bodyToFlux(HotelReviewDto.class)
            .collectList()
            .block();
    }

}
