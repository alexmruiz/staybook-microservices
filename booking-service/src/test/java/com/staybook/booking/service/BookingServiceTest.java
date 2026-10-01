package com.staybook.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.staybook.booking.client.HotelsClient;
import com.staybook.booking.client.ReviewsClient;
import com.staybook.booking.entity.RoomAvailability;
import com.staybook.booking.mapper.RoomAvailabilityMapper;

import com.staybook.booking.dto.request.BookingRequestDto;
import com.staybook.booking.dto.response.BookingDetailResponseDto;
import com.staybook.booking.dto.response.BookingResponseDto;
import com.staybook.booking.dto.response.HotelSummaryDto;
import com.staybook.booking.dto.response.ReviewSummaryDto;
import com.staybook.booking.entity.Booking;
import com.staybook.booking.enums.BookingStatus;
import com.staybook.booking.exception.BookingNotFoundException;
import com.staybook.booking.exception.InvalidBookingStateException;
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
        private Long userId;

        @BeforeEach
        void setUp() {
                userId = 1L;
                Long hotelId = 1L;
                Long roomTypeId = 1L;
                LocalDate checkIn = LocalDate.of(2026, 11, 5);
                LocalDate checkOut = LocalDate.of(2026, 11, 6);
                BookingStatus status = BookingStatus.CONFIRMED;
                BigDecimal price = BigDecimal.valueOf(100.00);
                LocalDateTime createdAt = LocalDateTime.of(2026, 11, 1, 10, 0);
                LocalDateTime updatedAt = LocalDateTime.of(2026, 11, 5, 10, 0);
                String bookingReference = UUID.randomUUID().toString();

                booking = new Booking(hotelId, roomTypeId, checkIn, checkOut, 3);
                booking.setUserId(userId);
                response = new BookingResponseDto(1L, userId, hotelId, roomTypeId, checkIn, checkOut, status, price,
                                createdAt,
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

                BookingResponseDto result = service.create(userId, request);

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
                service.create(userId, request);

                // Assert that saveAllUpdated received an entity with the original id and
                // decremented quantity
                @SuppressWarnings({ "unchecked", "rawtypes" })
                ArgumentCaptor<java.util.List<RoomAvailability>> captor = (ArgumentCaptor) ArgumentCaptor
                                .forClass(java.util.List.class);
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
                                () -> service.create(userId, badRequest));

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

        @Test
        void cancelBooking_shouldReincrementAvailability_andBeIdempotent() {
                // Arrange
                Long bookingId = 1L;
                booking.setId(bookingId);
                booking.setStatus(BookingStatus.PENDING);

                // availability before cancel
                RoomAvailability availability = new RoomAvailability(booking.getHotelId(), booking.getRoomTypeId(),
                                booking.getCheckInDate(), 5, BigDecimal.valueOf(100.00));
                availability.setId(10L);

                when(repository.findByIdForUpdate(bookingId)).thenReturn(Optional.of(booking));

                // cancel uses requestedRooms = 0 to fetch the full range (see implementation)
                when(roomAvailabilityService.getAvailableRoomsForUpdate(booking.getHotelId(), booking.getCheckInDate(),
                                booking.getCheckOutDate(), 0, booking.getRoomTypeId()))
                                .thenReturn(java.util.List.of(availability));

                // Capture original available quantity before the service mutates the entity
                int originalQuantity = availability.getAvailableQuantity();

                when(repository.save(booking)).thenReturn(booking);

                BookingResponseDto cancelledResponse = new BookingResponseDto(bookingId, booking.getUserId(),
                                booking.getHotelId(),
                                booking.getRoomTypeId(), booking.getCheckInDate(), booking.getCheckOutDate(),
                                BookingStatus.CANCELLED,
                                BigDecimal.valueOf(0), null, null, "ref", booking.getRoomsRequested());

                when(mapper.toResponseDto(booking)).thenReturn(cancelledResponse);

                // Act - first cancel should succeed
                BookingResponseDto result = service.cancelBooking(bookingId);

                // Assert
                assertNotNull(result);
                assertEquals(BookingStatus.CANCELLED, booking.getStatus());

                @SuppressWarnings({ "unchecked", "rawtypes" })
                ArgumentCaptor<java.util.List<RoomAvailability>> captor = (ArgumentCaptor) ArgumentCaptor
                                .forClass(java.util.List.class);
                verify(roomAvailabilityService).saveAllUpdated(captor.capture());

                List<RoomAvailability> saved = captor.getValue();
                // quantity increased by roomsRequested (use original snapshot)
                assertEquals(originalQuantity + booking.getRoomsRequested(), saved.get(0).getAvailableQuantity());

                verify(repository).save(booking);

                // Act - second cancel should fail (idempotency)
                InvalidBookingStateException ex = assertThrows(InvalidBookingStateException.class,
                                () -> service.cancelBooking(bookingId));
                assertEquals("La reserva ya ha sido cancelada", ex.getMessage());

                // saveAllUpdated must have been called only once
                verify(roomAvailabilityService, times(1)).saveAllUpdated(any());
        }

        @Test
        void findAll_returnsMappedPage() {
                Pageable pageable = PageRequest.of(0, 10);

                Page<Booking> bookingPage = new PageImpl<>(List.of(booking), pageable, 1);

                // DTO esperado (puede ser una instancia real o mock)
                BookingResponseDto expectedDto = mock(BookingResponseDto.class);

                when(repository.findAllByUserId(userId, pageable)).thenReturn(bookingPage);
                when(mapper.toResponseDto(booking)).thenReturn(expectedDto);

                Page<BookingResponseDto> result = service.findAll(pageable, userId);

                assertEquals(1, result.getTotalElements());
                assertSame(expectedDto, result.getContent().get(0));

                verify(repository).findAllByUserId(userId, pageable);
                verify(mapper).toResponseDto(booking);
        }

        @Nested
        class BookingDetailResponseDtoTest {

                @Test
                void getBookingDetails_whenBookingExists_andClientsReturnData_shouldReturnDetailDto() {
                        Long id = 1L;
                        booking.setId(id);

                        HotelSummaryDto hotelDto = new HotelSummaryDto(booking.getHotelId(), "Hotel X", 4, List.of());
                        ReviewSummaryDto reviewDto = new ReviewSummaryDto(booking.getHotelId(), 2L, 4.5, "Nice");

                        when(repository.findById(id)).thenReturn(Optional.of(booking));
                        when(mapper.toResponseDto(booking)).thenReturn(response);
                        when(hotelsClient.getHotelById(booking.getHotelId())).thenReturn(hotelDto);
                        when(reviewsClient.getReviewsByHotelId(booking.getHotelId())).thenReturn(List.of(reviewDto));

                        BookingDetailResponseDto result = service.getBookingDetails(id, userId);

                        assertNotNull(result);
                        assertSame(response, result.booking());
                        assertEquals(hotelDto, result.hotel());
                        assertEquals(List.of(reviewDto), result.reviews());

                        verify(repository).findById(id);
                        verify(mapper).toResponseDto(booking);
                        verify(hotelsClient).getHotelById(booking.getHotelId());
                        verify(reviewsClient).getReviewsByHotelId(booking.getHotelId());
                }

                @Test
                void getBookingDetails_whenHotelClientFails_shouldReturnNullHotelAndReviewsPresent() {
                        Long id = 1L;
                        booking.setId(id);

                        ReviewSummaryDto reviewDto = new ReviewSummaryDto(booking.getHotelId(), 2L, 4.5, "Nice");

                        when(repository.findById(id)).thenReturn(Optional.of(booking));
                        when(mapper.toResponseDto(booking)).thenReturn(response);
                        when(hotelsClient.getHotelById(booking.getHotelId())).thenThrow(new RuntimeException("down"));
                        when(reviewsClient.getReviewsByHotelId(booking.getHotelId())).thenReturn(List.of(reviewDto));

                        BookingDetailResponseDto result = service.getBookingDetails(id, userId);

                        assertNotNull(result);
                        assertSame(response, result.booking());
                        assertNull(result.hotel());
                        assertEquals(List.of(reviewDto), result.reviews());

                        verify(hotelsClient).getHotelById(booking.getHotelId());
                        verify(reviewsClient).getReviewsByHotelId(booking.getHotelId());
                }

                @Test
                void getBookingDetails_whenReviewsClientFails_shouldReturnEmptyReviewsAndHotelPresent() {
                        Long id = 1L;
                        booking.setId(id);

                        HotelSummaryDto hotelDto = new HotelSummaryDto(booking.getHotelId(), "Hotel X", 4, List.of());

                        when(repository.findById(id)).thenReturn(Optional.of(booking));
                        when(mapper.toResponseDto(booking)).thenReturn(response);
                        when(hotelsClient.getHotelById(booking.getHotelId())).thenReturn(hotelDto);
                        when(reviewsClient.getReviewsByHotelId(booking.getHotelId()))
                                        .thenThrow(new RuntimeException("down"));

                        BookingDetailResponseDto result = service.getBookingDetails(userId, id);

                        assertNotNull(result);
                        assertSame(response, result.booking());
                        assertEquals(hotelDto, result.hotel());
                        assertTrue(result.reviews().isEmpty());

                        verify(hotelsClient).getHotelById(booking.getHotelId());
                        verify(reviewsClient).getReviewsByHotelId(booking.getHotelId());
                }

                @Test
                void getBookingDetails_whenBookingNotFound_shouldThrow() {
                        Long id = 99L;
                        when(repository.findById(id)).thenReturn(Optional.empty());

                        BookingNotFoundException ex = assertThrows(BookingNotFoundException.class,
                                        () -> service.getBookingDetails(id, userId));
                        assertEquals("Reserva no encontrada con id: 99", ex.getMessage());

                        verify(repository).findById(id);
                        verifyNoInteractions(hotelsClient);
                        verifyNoInteractions(reviewsClient);
                }

        }

        @Nested
        class confirmBookingTest {

                @Test
                void confirmBooking_whenPending_shouldConfirmAndReturnDto() {
                        Long bookingId = 1L;
                        booking.setId(bookingId);
                        booking.setStatus(BookingStatus.PENDING);

                        when(repository.findByIdForUpdate(bookingId)).thenReturn(Optional.of(booking));
                        when(repository.save(booking)).thenReturn(booking);

                        BookingResponseDto confirmedDto = new BookingResponseDto(bookingId, booking.getUserId(),
                                        booking.getHotelId(), booking.getRoomTypeId(), booking.getCheckInDate(),
                                        booking.getCheckOutDate(), BookingStatus.CONFIRMED, BigDecimal.ZERO, null, null,
                                        "ref", booking.getRoomsRequested());
                        when(mapper.toResponseDto(booking)).thenReturn(confirmedDto);

                        BookingResponseDto result = service.confirmBooking(bookingId);

                        assertNotNull(result);
                        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
                        assertSame(confirmedDto, result);

                        verify(repository).findByIdForUpdate(bookingId);
                        verify(repository).save(booking);
                        verify(mapper).toResponseDto(booking);
                }

                @Test
                void confirmBooking_whenNotFound_shouldThrowBookingNotFoundException() {
                        Long bookingId = 99L;
                        when(repository.findByIdForUpdate(bookingId)).thenReturn(Optional.empty());

                        BookingNotFoundException ex = assertThrows(BookingNotFoundException.class,
                                        () -> service.confirmBooking(bookingId));
                        assertEquals("Reserva no encontrada con id: 99", ex.getMessage());

                        verify(repository).findByIdForUpdate(bookingId);
                        verify(mapper, never()).toResponseDto(any());
                        verify(repository, never()).save(any());
                }

                @Test
                void confirmBooking_whenStatusNotPending_shouldThrowInvalidBookingStateException() {
                        Long bookingId = 1L;
                        booking.setId(bookingId);
                        booking.setStatus(BookingStatus.CONFIRMED); // estado no permitido

                        when(repository.findByIdForUpdate(bookingId)).thenReturn(Optional.of(booking));

                        InvalidBookingStateException ex = assertThrows(InvalidBookingStateException.class,
                                        () -> service.confirmBooking(bookingId));
                        assertEquals("El estado de la reserva no permite la confirmación", ex.getMessage());

                        verify(repository).findByIdForUpdate(bookingId);
                        verify(repository, never()).save(any());
                        verify(mapper, never()).toResponseDto(any());
                }
        }

}
