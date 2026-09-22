package com.staybook.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.staybook.booking.dto.response.RoomAvailabilityResponseDto;
import com.staybook.booking.entity.RoomAvailability;
import com.staybook.booking.exception.InvalidDateRangeException;
import com.staybook.booking.exception.RoomNotAvailableException;
import com.staybook.booking.mapper.RoomAvailabilityMapper;
import com.staybook.booking.repository.RoomAvailabilityRepository;


@ExtendWith (MockitoExtension.class)
class RoomAvailabilityServiceTest {
    
    @Mock  
    private RoomAvailabilityRepository repository;

    @Mock  
    private RoomAvailabilityMapper mapper;

    @InjectMocks 
    private RoomAvailabilityService service;

    @Test 
    void shouldReturnAvailabilityList_WhenValidDateRanger() {
        Long hotelId = 1L;
        Long roomTypeId = 1L;
        LocalDate date = LocalDate.of(2026, 5, 5);
        LocalDate startDate = LocalDate.of(2026, 5, 5);
        LocalDate endDate = LocalDate.of(2026, 5, 7);
        int requestedQuantity = 2;

        RoomAvailability roomAvailability = new RoomAvailability(hotelId, roomTypeId, date, 2, BigDecimal.valueOf(100.00));
        RoomAvailabilityResponseDto response = new RoomAvailabilityResponseDto(1L, hotelId, roomTypeId, date, requestedQuantity, BigDecimal.valueOf(100.00));

        when(repository.getAvailableRooms(
                hotelId, startDate, endDate, requestedQuantity))
                .thenReturn(List.of(roomAvailability));

        when(mapper.toResponseDto(roomAvailability))
                .thenReturn(response);

        List<RoomAvailabilityResponseDto> result = service.getAvailableRooms(hotelId, startDate, endDate, 2);

        assertNotNull(result);
        assertEquals(date, result.get(0).date());
        verify(repository).getAvailableRooms(hotelId, startDate, endDate, 2);
    }

    @Test
    @DisplayName("Debe lanzar InvalidDateRangeException si la fecha de inicio es posterior a la de fin")
    void shouldThrowExceptionWhenStartDateIsAfterEndDate() {
        // Given
        Long hotelId = 1L;
        LocalDate startDate = LocalDate.of(2026, 6, 5);
        LocalDate endDate = LocalDate.of(2026, 6, 1); // Inválido

        // When / Then
        assertThrows(InvalidDateRangeException.class, (()->{service.getAvailableRooms(hotelId, startDate, endDate, 0);}));
    }

    @Test
    @DisplayName("Debe lanzar RoomNotAvailableException si no hay habitaciones disponibles")
    void shouldThrowExceptionWhenQuantityIsEmpty() {
        // Given
        Long hotelId = 1L;
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 5); // válido

        when(repository.getAvailableRooms(
                hotelId, startDate, endDate, 1))
                .thenReturn(List.of());

        // When / Then
        assertThrows(RoomNotAvailableException.class, () -> service.getAvailableRooms(hotelId, startDate, endDate, 1));
    }

}
