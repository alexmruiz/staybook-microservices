package com.staybook.booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.staybook.booking.client.HotelsClient;

@Configuration
public class RestClientConfig {

    @Value("${hotels-service.base-url}")
    private String hotelServiceBaseUrl;

    @Bean
    public RestClient hotelRestClient() {
        return RestClient.builder()
                .baseUrl(hotelServiceBaseUrl)
                .build();
    }

    @Bean
    public HotelsClient hotelsClient(RestClient hotelsRestClient) {
        RestClientAdapter adapter = RestClientAdapter.create(hotelsRestClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(HotelsClient.class);
    }

}
