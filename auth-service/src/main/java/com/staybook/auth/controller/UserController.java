package com.staybook.auth.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staybook.auth.dto.request.RegisterRequestDto;
import com.staybook.auth.dto.response.UserResponseDto;
import com.staybook.auth.service.AuthService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthService service;

    public UserController(AuthService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public UserResponseDto getMyProfile(Principal principal) {
        String userEmail = principal.getName();
        return service.getEmailUser(userEmail);
    }

    @PutMapping("/me")
    public UserResponseDto updateMyProfile(Principal principal, @RequestBody RegisterRequestDto requestDto) {
        String userEmail = principal.getName();
        return service.updateUserProfile(userEmail, requestDto);
    }
}
