package com.hotelsbook.services.com_hotelsbook_services.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.hotelsbook.services.com_hotelsbook_services.client.CountriesNowClient;

@Configuration
public class CountriesNowConfig {

    @Value("${countries-now.base-url}")
    private String url;

    @Bean
    public CountriesNowClient countriesNowClient() {
        RestClient restClient = RestClient.builder().baseUrl(url).build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(CountriesNowClient.class);
    }
}
