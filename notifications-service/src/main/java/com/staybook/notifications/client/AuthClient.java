package com.staybook.notifications.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.staybook.notifications.config.AuthClientConfig;
import com.staybook.notifications.dto.UserSummaryResponseDto;

@FeignClient(name = "auth-service", url = "${auth.service.url:http://localhost:8083}", configuration = AuthClientConfig.class)
public interface AuthClient {

    @GetMapping("/internal/users/{userId}")
    UserSummaryResponseDto getUserById(@PathVariable("userId") Long userId);
}
