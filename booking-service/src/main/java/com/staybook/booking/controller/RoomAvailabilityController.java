package com.staybook.booking.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.staybook.booking.dto.response.RoomAvailabilityResponseDto;
import com.staybook.booking.service.RoomAvailabilityService;

import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/room-availability")
@Validated 
public class RoomAvailabilityController {

    private final RoomAvailabilityService service;

    public RoomAvailabilityController(RoomAvailabilityService service) {
        this.service = service;
    }

    @GetMapping
    List<RoomAvailabilityResponseDto> getAvailableRooms(
            @RequestParam Long hotelId,
            @RequestParam @Positive Long roomTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam @Positive Integer requestedRooms) {
        return service.getAvailableRooms(hotelId, roomTypeId, startDate, endDate, requestedRooms);
    }

}
