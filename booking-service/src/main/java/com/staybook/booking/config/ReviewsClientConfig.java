package com.staybook.booking.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.staybook.booking.client.ReviewsClient;

@Configuration
public class ReviewsClientConfig {

    @Value("${reviews-service.base-url}")
    private String reviewServiceBaseUrl;

    @Bean
    public RestClient reviewRestClient() {
        return RestClient.builder()
                .baseUrl(reviewServiceBaseUrl)
                .build();
    }

    @Bean
    public ReviewsClient reviewsClient(@Qualifier("reviewRestClient") RestClient reviewRestClient) {
        RestClientAdapter adapter = RestClientAdapter.create(reviewRestClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ReviewsClient.class);
    }
}
