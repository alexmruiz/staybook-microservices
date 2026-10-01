package com.staybook.booking.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody BookingRequestDto requestDto){
        return service.create(userId(jwt), requestDto);
    }

    @GetMapping("/{bookingId}") 
    public BookingResponseDto findById(@AuthenticationPrincipal Jwt jwt, @PathVariable("bookingId") Long bookingId) {
        return service.findById(userId(jwt), bookingId);
    }

    @GetMapping
    public Page<BookingResponseDto> findAll(@AuthenticationPrincipal Jwt jwt, Pageable pageable) {
        return service.findAll(pageable, userId(jwt));
    }

    @GetMapping("/{bookingId}/details")
    public BookingDetailResponseDto getBookingDetails(@PathVariable("bookingId") Long bookingId,
            @AuthenticationPrincipal Jwt jwt) {
        return service.getBookingDetails(bookingId, userId(jwt));
    }

    @PostMapping("/{bookingId}/confirm")
    public BookingResponseDto confirmBooking (@PathVariable("bookingId") Long bookingId) {
        return service.confirmBooking(bookingId);
    }

    @PostMapping("/{bookingId}/cancel")
    public BookingResponseDto cancelBooking (@PathVariable("bookingId") Long bookingId) {
        return service.cancelBooking(bookingId);
    }

    private Long userId(Jwt jwt) {
        Object claim = jwt == null ? null : jwt.getClaim("userId");
        if (claim instanceof Number number) {
            return number.longValue();
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT sin claim userId válido");
    }
}
