package com.staybook.booking.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.staybook.booking.dto.response.RoomAvailabilityResponseDto;
import com.staybook.booking.service.RoomAvailabilityService;

@WebMvcTest (RoomAvailabilityController.class)
public class RoomAvailabilityControllerTest {
    
    @Autowired 
    private MockMvc mockMvc;

    @MockitoBean 
    private RoomAvailabilityService service;

    @Test 
    void shouldReturnAvailableRoomsSuccessfully() throws Exception {
        Long hotelId = 1L;
        LocalDate startDate = LocalDate.of(2026, 5, 1);
        LocalDate endDate = LocalDate.of(2026, 5, 2);
        Integer availableQuantity = 2;

        RoomAvailabilityResponseDto response = new RoomAvailabilityResponseDto(1L, hotelId, 2L, startDate, availableQuantity, BigDecimal.valueOf(100.00));

        when(service.getAvailableRooms(hotelId, startDate, endDate, availableQuantity)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/room-availability/1/2026-05-01/2026-05-02/2")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].date").value("2026-05-01"))
            .andExpect(jsonPath("$[0].availableQuantity").value(2))
            .andExpect(jsonPath("$[0].price").value(100.00));

    }



}
