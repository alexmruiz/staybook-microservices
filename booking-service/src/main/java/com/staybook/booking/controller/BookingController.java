package com.staybook.booking.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.staybook.booking.dto.request.BookingRequestDto;
import com.staybook.booking.dto.response.BookingDetailResponseDto;
import com.staybook.booking.dto.response.BookingResponseDto;
import com.staybook.booking.service.BookingService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/bookings")
@Validated 
public class BookingController {
    
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping 
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto save(
        @RequestHeader("X-User-Id") Long userId,
        @Valid @RequestBody BookingRequestDto requestDto){
        return service.create(userId, requestDto);
    }

    @GetMapping("/{bookingId}") 
    public BookingResponseDto findById(@RequestHeader("X-User-Id") Long userId, @PathVariable("bookingId") Long bookingId) {
        return service.findById(userId, bookingId);
    }

    @GetMapping
    public Page<BookingResponseDto> findAll(@RequestHeader("X-User-Id") Long userId, Pageable pageable) {
        return service.findAll(pageable, userId);
    }

    @GetMapping("/{bookingId}/details")
    public BookingDetailResponseDto getBookingDetails(@PathVariable("bookingId") Long bookingId, @RequestHeader("X-User-Id") Long userId) {
        return service.getBookingDetails(bookingId, userId);
    }

    @PostMapping("/{bookingId}/confirm")
    public BookingResponseDto confirmBooking (@PathVariable("bookingId") Long bookingId) {
        return service.confirmBooking(bookingId);
    }

    @PostMapping("/{bookingId}/cancel")
    public BookingResponseDto cancelBooking (@PathVariable("bookingId") Long bookingId) {
        return service.cancelBooking(bookingId);
    }
}
