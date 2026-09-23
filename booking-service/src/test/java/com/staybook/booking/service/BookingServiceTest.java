package com.staybook.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.never;

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
import com.staybook.booking.exception.BookingNotFoundException;
import com.staybook.booking.exception.InvalidDateRangeException;
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
        LocalDate checkOut = LocalDate.of(2026, 11, 6);
        BookingStatus status = BookingStatus.CONFIRMED;
        BigDecimal price = BigDecimal.valueOf(100.00);
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 11, 5, 10, 0);
        String bookingReference = UUID.randomUUID().toString();

        booking = new Booking(userId, hotelId, roomTypeId, checkIn, checkOut);
        response = new BookingResponseDto(1L, userId, hotelId, roomTypeId, checkIn, checkOut, status, price, createdAt,
                updatedAt, bookingReference);
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

    @Test
    void create_whenCheckInAfterCheckOut_shouldThrowInvalidDateRangeException() {
        // check-in posterior a check-out
        BookingRequestDto badRequest = new BookingRequestDto(
                1L,
                1L,
                1L,
                LocalDate.of(2026, 11, 6), // checkIn
                LocalDate.of(2026, 11, 5) // checkOut
        );

        InvalidDateRangeException ex = assertThrows(
                InvalidDateRangeException.class,
                () -> service.create(badRequest));

        assertEquals("La fecha de salida debe ser posterior a la fecha de entrada", ex.getMessage());

        // comprobar que no se invocó el mapper ni el repositorio
        verifyNoInteractions(mapper);
        verifyNoInteractions(repository);
    }

    @Test
    void findById_WhenIdExists_ShouldReturnBooking() {
        when(repository.findById(1L)).thenReturn(Optional.of(booking));
        when(mapper.toResponseDto(booking)).thenReturn(response);

        BookingResponseDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(result, response);

        verify(repository).findById(1L);
        verify(mapper).toResponseDto(booking);
    }

    @Test
    void findById_WhenIdDoesNotExist_ShouldThrowException() {
        Long idInexistente = 99L;

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class, () -> service.findById(idInexistente));

        assertEquals("Reseña no encontrada con id: 99", exception.getMessage());

        verify(repository).findById(idInexistente);
        verify(mapper, never()).toResponseDto(any());
    }
}
