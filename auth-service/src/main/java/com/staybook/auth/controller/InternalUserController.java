package com.staybook.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staybook.auth.dto.response.UserBookingResponseDto;
import com.staybook.auth.service.AuthService;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final AuthService service;

    public InternalUserController(AuthService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public UserBookingResponseDto getUserById(@PathVariable("userId") Long userId) {

        return service.getUserById(userId);
    }
}
