package com.hotelsbook.reviews.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotelsbook.reviews.dto.request.ReviewRequestDto;
import com.hotelsbook.reviews.dto.response.ReviewResponseDto;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.hotelsbook.reviews.entity.ReviewEntity;
import com.hotelsbook.reviews.exception.ReviewNotFoundException;
import com.hotelsbook.reviews.mapper.ReviewMapper;
import com.hotelsbook.reviews.repository.ReviewRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository repository;

    @Mock
    private ReviewMapper mapper;

    @InjectMocks
    private ReviewService service;

    private ReviewEntity review;
    private ReviewRequestDto request;
    private ReviewResponseDto response;

    @BeforeEach
    void setUp() {
        review = new ReviewEntity(1L, 1L, 4.00);
        request = new ReviewRequestDto(1L, 1L, 4.00, "ss");
        response = new ReviewResponseDto(1L, 1L, 1L, 4.00, LocalDateTime.parse("2026-08-24T17:30:00"));
    }

    @Test
    void create_ShouldReturnReviewResponseDto() {

        when(mapper.toEntity(request)).thenReturn(review);
        when(repository.save(review)).thenReturn(review);
        when(mapper.toResponseDto(review)).thenReturn(response);

        ReviewResponseDto result = service.create(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(4.00, result.qualification());

        verify(mapper).toEntity(request);
        verify(repository).save(review);
        verify(mapper).toResponseDto(review);
    }

    @Test
    void findAll_ShouldReturnReviewList() {

        when(repository.findAll()).thenReturn(List.of(review));
        when(mapper.toResponseDto(review)).thenReturn(response);

        List<ReviewResponseDto> result = service.findAll();

        assertTrue(!result.isEmpty());
        assertNotNull(result);

        verify(repository).findAll();
        verify(mapper).toResponseDto(review);
    }

    @Test
    void findById_WhenIdExists_ShouldReturnReview() {
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(mapper.toResponseDto(review)).thenReturn(response);

        ReviewResponseDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(4.00, result.qualification());

        verify(repository).findById(1L);
        verify(mapper).toResponseDto(review);
    }

    @Test
    void findById_WhenIdDoesNotExist_ShouldThrowException() {
        Long id = 99L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> service.findById(id));

        assertEquals("Reseña no encontrada con Id: " + id, exception.getMessage());

        verify(repository).findById(id);
        verify(mapper, never()).toResponseDto(any());
    }

    @Test
    void update_WhenIdExists_ShouldUpdateAndReturnResponse() {
        Long id = 1L;
        ReviewRequestDto requestUpdate = new ReviewRequestDto(2L, 2L, 4.00, "ss");

        when(repository.findById(id)).thenReturn(Optional.of(review));
        when(repository.save(any())).thenAnswer(inv -> {
            ReviewEntity saved = inv.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(mapper.toResponseDto(any())).thenAnswer(inv -> {
            ReviewEntity e = inv.getArgument(0);
            return new ReviewResponseDto(e.getId(), e.getHotelId(), e.getUserId(), e.getQualification(), response.createdAt());
        });

        ReviewResponseDto result = service.update(id, requestUpdate);

        assertNotNull(result);
        assertEquals(2L, result.hotelId());

        verify(repository).findById(id);
        verify(repository).save(review);
        verify(mapper).toResponseDto(review);
    }

    @Test
    void update_WhenIdDoesNotExist_ShouldThrowException() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        ReviewNotFoundException exception = assertThrows(
                ReviewNotFoundException.class,
                () -> service.update(id, request));

        assertEquals("Reseña no encontrada", exception.getMessage());
        verify(repository).findById(id);
        verify(repository, never()).save(any());
    }

    @Test
    void delete_WhenIdExists_ShouldDeleteSuccessfully() {
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);
        doNothing().when(repository).deleteById(id);

        assertDoesNotThrow(() -> service.delete(id));

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    void delete_WhenIdDoesNotExist_ShouldThrowException() {
        Long id = 99L;
        when(repository.existsById(id)).thenReturn(false);

        ReviewNotFoundException exception = assertThrows(
                ReviewNotFoundException.class,
                () -> service.delete(id));

        assertEquals("No se encontró la reseña con id: " + id, exception.getMessage());
        verify(repository).existsById(id);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void findByHotelId_WhenExists_ReturnsList() {
        Long hotelId = 1L;
        when(repository.findByHotelId(hotelId)).thenReturn(List.of(review));
        when(mapper.toResponseDto(review)).thenReturn(response);

        List<ReviewResponseDto> result = service.findByHotelId(hotelId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(hotelId, result.get(0).hotelId());

        verify(repository).findByHotelId(hotelId);
        verify(mapper).toResponseDto(review);
    }

    @Test
    void findByHotelId_WhenNoReviews_ReturnsEmptyList() {
        Long hotelId = 99L;
        when(repository.findByHotelId(hotelId)).thenReturn(List.of());

        List<ReviewResponseDto> result = service.findByHotelId(hotelId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findByHotelId(hotelId);
        verify(mapper, never()).toResponseDto(any());
    }

}
