package com.staybook.booking.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.staybook.booking.dto.response.RoomAvailabilityResponseDto;
import com.staybook.booking.service.RoomAvailabilityService;

@WebMvcTest(RoomAvailabilityController.class)
class RoomAvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomAvailabilityService roomAvailabilityService;

    private RoomAvailabilityResponseDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new RoomAvailabilityResponseDto(
            1L, 1L, 2L, LocalDate.of(2026, 11, 1), 2, BigDecimal.valueOf(100.00)
        );
    }

    @Test
    @DisplayName("Debe retornar la lista de habitaciones disponibles con estado 200 OK")
    void getAvailableRooms_WhenValidParams_ShouldReturn200Ok() throws Exception {
        Long hotelId = 1L;
        Long roomTypeId = 3L;
        LocalDate startDate = LocalDate.of(2026, 11, 1);
        LocalDate endDate = LocalDate.of(2026, 11, 2);
        Integer availableQuantity = 2;

        when(roomAvailabilityService.getAvailableRooms(hotelId, roomTypeId, startDate, endDate, availableQuantity))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/room-availability")
                .param("hotelId", hotelId.toString())
                .param("roomTypeId", roomTypeId.toString())
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("requestedRooms", String.valueOf(availableQuantity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].hotelId").value(1))
                .andExpect(jsonPath("$[0].roomTypeId").value(2))
                .andExpect(jsonPath("$[0].date").value("2026-11-01"))
                .andExpect(jsonPath("$[0].availableQuantity").value(2))
                .andExpect(jsonPath("$[0].price").value(100.00));

        verify(roomAvailabilityService).getAvailableRooms(hotelId, roomTypeId, startDate, endDate, availableQuantity);
    }
}
