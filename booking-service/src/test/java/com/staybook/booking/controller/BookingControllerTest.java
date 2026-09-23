package com.staybook.booking.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import com.staybook.booking.exception.BookingNotFoundException;
import com.staybook.booking.service.BookingService;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


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
        responseDto = new BookingResponseDto(1L, userId, hotelId, roomTypeId, checkIn, checkOut, status, price,
                createdAt,
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

    @Test
    void findById_WhenExists_ShouldReturn200Ok() throws Exception {
        when(service.findById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/bookings/{bookingId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1));

        verify(service).findById(1L);
    }

    @Test
    @DisplayName("Debe devolver el status configurado por la excepción si no existe")
    void findById_WhenNotFound_ShouldReturnError() throws Exception {
        when(service.findById(999L)).thenThrow(new BookingNotFoundException("Reserva no encontrada con id: "));

        mockMvc.perform(get("/api/bookings/{bookingId}", 999L))
                .andExpect(status().isNotFound());

        verify(service).findById(999L);
    }

    @Test
    void findAll_WhenUserHasBookings_ReturnsPage() throws Exception {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<BookingResponseDto> page = new PageImpl<>(List.of(responseDto), pageable, 1);

        when(service.findAll(any(Pageable.class), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/api/bookings/all-bookings/{userId}", userId)
                .param("page", "0")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].userId").value(1));

        verify(service).findAll(any(Pageable.class), eq(userId));
    }

    @Test
    void findAll_WhenNoBookings_ReturnsEmptyPage() throws Exception {
        Long userId = 2L;
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<BookingResponseDto> empty = new PageImpl<>(List.of(), pageable, 0);

        when(service.findAll(any(Pageable.class), eq(userId))).thenReturn(empty);

        mockMvc.perform(get("/api/bookings/all-bookings/{userId}", userId)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(service).findAll(any(Pageable.class), eq(userId));
    }

}
