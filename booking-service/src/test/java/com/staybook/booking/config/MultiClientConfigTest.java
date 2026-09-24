package com.staybook.booking.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.assertj.core.api.Assertions.assertThat;

class MultiClientConfigTest {
  private final ApplicationContextRunner runner = new ApplicationContextRunner();

  @Test
  void bothRestClientsExist_andAreDistinct_whenBothBaseUrlsProvided() {
    runner
      .withUserConfiguration(
          com.staybook.booking.config.HotelsClientConfig.class,
          com.staybook.booking.config.ReviewsClientConfig.class)
      .withPropertyValues(
          "hotels-service.base-url=http://hotels:8080",
          "reviews-service.base-url=http://reviews:8081")
      .run(ctx -> {
        assertThat(ctx).hasBean("hotelRestClient");
        assertThat(ctx).hasBean("reviewRestClient");
        assertThat(ctx).hasBean("hotelsClient");
        assertThat(ctx).hasBean("reviewsClient");

        Object hotelsRest = ctx.getBean("hotelRestClient");
        Object reviewsRest = ctx.getBean("reviewRestClient");
        assertThat(hotelsRest).isNotSameAs(reviewsRest);

        Object hotelsClient = ctx.getBean("hotelsClient");
        Object reviewsClient = ctx.getBean("reviewsClient");
        assertThat(hotelsClient).isNotSameAs(reviewsClient);
      });
  }
}
