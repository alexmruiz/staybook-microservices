package com.staybook.booking.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staybook.booking.dto.response.RoomAvailabilityResponseDto;
import com.staybook.booking.service.RoomAvailabilityService;

@RestController
@RequestMapping("/api/room-availability")
public class RoomAvailabilityController {

    private final RoomAvailabilityService service;

    public RoomAvailabilityController(RoomAvailabilityService service) {
        this.service = service;
    }

    @GetMapping("{hotelId}/{startDate}/{endDate}/{availableQuantity}")
    List<RoomAvailabilityResponseDto> getAvailableRooms(
            @PathVariable("hotelId") Long hotelId,
            @PathVariable("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PathVariable("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PathVariable("availableQuantity") int availableQuantity) {
        return service.getAvailableRooms(hotelId, startDate, endDate, availableQuantity);
    }

}
