package com.staybook.notifications.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.staybook.notifications.dto.UserSummaryResponseDto;

@FeignClient(name = "users-service", url = "${users.service.url:http://localhost:8083}")
public interface UserClient {

    @GetMapping("/api/users/{userId}")
    UserSummaryResponseDto getUserById(@PathVariable("userId") Long userId);
}
