package com.staybook.booking.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewsClientConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    void createsReviewRestClientAndReviewsClient_whenBaseUrlProvided() {
        contextRunner
            .withUserConfiguration(com.staybook.booking.config.ReviewsClientConfig.class)
            .withPropertyValues("reviews-service.base-url=http://localhost:8081")
            .run(ctx -> {
                assertThat(ctx).hasBean("reviewRestClient");
                assertThat(ctx).hasBean("reviewsClient");
                Object reviewsClient = ctx.getBean("reviewsClient");
                assertThat(reviewsClient).isInstanceOf(com.staybook.booking.client.ReviewsClient.class);
            });
    }

}

