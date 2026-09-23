package com.staybook.booking.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staybook.booking.dto.request.BookingRequestDto;
import com.staybook.booking.dto.response.BookingResponseDto;
import com.staybook.booking.entity.Booking;
import com.staybook.booking.enums.BookingStatus;
import com.staybook.booking.service.BookingService;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService service;

    Booking booking;
    private BookingRequestDto requestDto;
    private BookingResponseDto responseDto;

    @BeforeEach
    void setUp() {
        Long userId = 1L;
        Long hotelId = 1L;
        Long roomTypeId = 1L;
        LocalDate checkIn = LocalDate.of(2026, 11, 5);
        LocalDate checkOut = LocalDate.of(2026, 11, 5);
        BookingStatus status = BookingStatus.CONFIRMED;
        BigDecimal price = BigDecimal.valueOf(100.00);
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 11, 5, 10, 0);
        String bookingReference = UUID.randomUUID().toString();

        booking = new Booking(userId, hotelId, roomTypeId, checkIn, checkOut);
        responseDto = new BookingResponseDto(1L, userId, hotelId, roomTypeId, checkIn, checkOut, status, price, createdAt,
                updatedAt, bookingReference);
        requestDto = new BookingRequestDto(userId, hotelId, roomTypeId, checkIn, checkOut);
    }

    @Test
    void create_WhenValidRequest_ShouldReturn201Created() throws Exception {
        when(service.create(any(BookingRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED")); // compare as string

        verify(service).create(any(BookingRequestDto.class));
    }

}
