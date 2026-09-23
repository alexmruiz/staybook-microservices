package com.staybook.booking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.staybook.booking.dto.request.BookingRequestDto;
import com.staybook.booking.dto.response.BookingResponseDto;
import com.staybook.booking.service.BookingService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/bookings")
public class BookingController {
    
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping 
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto save(@Valid @RequestBody BookingRequestDto requestDto){
        return service.create(requestDto);
    }
}
