package com.staybook.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.staybook.booking.dto.request.BookingRequestDto;
import com.staybook.booking.dto.response.BookingResponseDto;
import com.staybook.booking.entity.Booking;
import com.staybook.booking.enums.BookingStatus;
import com.staybook.booking.mapper.BookingMapper;
import com.staybook.booking.repository.BookingRepository;

@ExtendWith(MockitoExtension.class) 
class BookingServiceTest {
    
    @Mock 
    private BookingRepository repository;

    @Mock
    private BookingMapper mapper;

    @InjectMocks 
    private BookingService service;

    private Booking booking;
    private BookingRequestDto request;
    private BookingResponseDto response;

    @BeforeEach 
    void setUp() {
        Long userId = 1L;
        Long hotelId = 1L;
        Long roomTypeId = 1L;
        LocalDate checkIn = LocalDate.of(2026, 11, 5);
        LocalDate checkOut = LocalDate.of(2026, 11, 5);
        BookingStatus status = BookingStatus.CONFIRMED;
        BigDecimal price = BigDecimal.valueOf(100.00);
        LocalDateTime createdAt = LocalDateTime.of(2026,11,1,10,0);
        LocalDateTime updatedAt = LocalDateTime.of(206,11,5,10,0);
        String bookingReference = UUID.randomUUID().toString();
        
        booking = new Booking(userId, hotelId, roomTypeId, checkIn, checkOut);
        response = new BookingResponseDto(1L, userId, roomTypeId, checkIn, checkOut, status, price, createdAt, updatedAt, bookingReference);
        request = new BookingRequestDto(userId, hotelId, roomTypeId, checkIn, checkOut);
    }

    @Test 
    void saved_shouldReturnResponseDto() {
        when(mapper.toEntity(request)).thenReturn(booking);
        when(repository.save(booking)).thenReturn(booking);
        when(mapper.toResponseDto(booking)).thenReturn(response);

        BookingResponseDto result = service.create(request);

        assertNotNull(result);
        assertEquals(1L, result.userId());
        assertEquals(1L, result.roomTypeId());

        verify(mapper).toEntity(request);
        verify(repository).save(booking);
        verify(mapper).toResponseDto(booking);
    }
}
