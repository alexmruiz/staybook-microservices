package com.staybook.booking.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.staybook.booking.client.HotelsClient;

@Configuration
public class HotelsClientConfig {

    @Value("${hotels-service.base-url}")
    private String hotelServiceBaseUrl;

    @Bean
    public RestClient hotelRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        return RestClient.builder()
                .baseUrl(hotelServiceBaseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Bean
    public HotelsClient hotelsClient(@Qualifier("hotelRestClient") RestClient hotelRestClient) {
        RestClientAdapter adapter = RestClientAdapter.create(hotelRestClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(HotelsClient.class);
    }

}
