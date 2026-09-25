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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.never;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.staybook.booking.client.HotelsClient;
import com.staybook.booking.client.ReviewsClient;
import com.staybook.booking.entity.RoomAvailability;
import com.staybook.booking.mapper.RoomAvailabilityMapper;

import com.staybook.booking.dto.request.BookingRequestDto;
import com.staybook.booking.dto.response.BookingResponseDto;
import com.staybook.booking.entity.Booking;
import com.staybook.booking.enums.BookingStatus;
import com.staybook.booking.exception.BookingNotFoundException;
import com.staybook.booking.exception.InvalidDateRangeException;
import com.staybook.booking.mapper.BookingMapper;
import com.staybook.booking.repository.BookingRepository;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository repository;

    @Mock
    private BookingMapper mapper;

    @Mock
    private RoomAvailabilityService roomAvailabilityService;

    @Mock
    private RoomAvailabilityMapper roomAvailabilityMapper;

    @Mock
    private ReviewsClient reviewsClient;

    @Mock
    private HotelsClient hotelsClient;

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

        booking = new Booking(userId, hotelId, roomTypeId, checkIn, checkOut, 3);
        response = new BookingResponseDto(1L, userId, hotelId, roomTypeId, checkIn, checkOut, status, price, createdAt,
                updatedAt, bookingReference, 3);
        request = new BookingRequestDto(userId, hotelId, roomTypeId, checkIn, checkOut, 3);
    }

    @Test
    void saved_shouldReturnResponseDto() {
        // Prepare availability for the date range (one night) as entity
        RoomAvailability availabilityEntity = new RoomAvailability(request.hotelId(), request.roomTypeId(),
                request.checkInDate(), request.roomsRequested(), BigDecimal.valueOf(100.00));
        availabilityEntity.setId(1L);

        when(roomAvailabilityService.getAvailableRoomsForUpdate(request.hotelId(), request.checkInDate(),
                request.checkOutDate(), request.roomsRequested(), request.roomTypeId()))
                .thenReturn(java.util.List.of(availabilityEntity));

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
    void create_ShouldDecrementAvailabilityWithCorrectId() {
        // Arrange: availability with an existing id (entity)
        RoomAvailability availabilityWithId = new RoomAvailability(request.hotelId(), request.roomTypeId(),
                request.checkInDate(), request.roomsRequested(), BigDecimal.valueOf(100.00));
        availabilityWithId.setId(99L);

        when(roomAvailabilityService.getAvailableRoomsForUpdate(request.hotelId(), request.checkInDate(),
                request.checkOutDate(), request.roomsRequested(), request.roomTypeId()))
                .thenReturn(java.util.List.of(availabilityWithId));

        when(mapper.toEntity(request)).thenReturn(booking);
        when(repository.save(booking)).thenReturn(booking);
        when(mapper.toResponseDto(booking)).thenReturn(response);

        // Capture original quantity before service mutates the entity
        int originalQuantity = availabilityWithId.getAvailableQuantity();

        // Act
        service.create(request);

        // Assert that saveAllUpdated received an entity with the original id and decremented quantity
        @SuppressWarnings({"unchecked", "rawtypes"})
        ArgumentCaptor<java.util.List<RoomAvailability>> captor = (ArgumentCaptor) ArgumentCaptor.forClass(java.util.List.class);
        verify(roomAvailabilityService).saveAllUpdated(captor.capture());

        List<RoomAvailability> saved = captor.getValue();
        assertEquals(99L, saved.get(0).getId());
        assertEquals(originalQuantity - request.roomsRequested(), saved.get(0).getAvailableQuantity());
    }

    @Test
    void create_whenCheckInAfterCheckOut_shouldThrowInvalidDateRangeException() {
        // check-in posterior a check-out
        BookingRequestDto badRequest = new BookingRequestDto(
                1L,
                1L,
                1L,
                LocalDate.of(2026, 11, 6), // checkIn
                LocalDate.of(2026, 11, 5), // checkOut
                3);

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

        assertEquals("Reserva no encontrada con id: 99", exception.getMessage());

        verify(repository).findById(idInexistente);
        verify(mapper, never()).toResponseDto(any());
    }
}
