package com.staybook.booking.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.assertj.core.api.Assertions.assertThat;

class HotelsClientConfigTest {

  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

  @Test
  void createsHotelRestClientAndHotelsClient_whenBaseUrlProvided() {
    contextRunner
      .withUserConfiguration(com.staybook.booking.config.HotelsClientConfig.class)
      .withPropertyValues("hotels-service.base-url=http://localhost:8082")
      .run(ctx -> {
        assertThat(ctx).hasBean("hotelRestClient");
        assertThat(ctx).hasBean("hotelsClient");
        Object hotelsClient = ctx.getBean("hotelsClient");
        assertThat(hotelsClient).isInstanceOf(com.staybook.booking.client.HotelsClient.class);
      });
  }
}