package com.hotelsbook.reviews.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelsbook.reviews.dto.request.ReviewRequestDto;
import com.hotelsbook.reviews.dto.response.ReviewResponseDto;
import com.hotelsbook.reviews.exception.ReviewNotFoundException;
import com.hotelsbook.reviews.service.ReviewService;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    private ReviewRequestDto requestDto;
    private ReviewResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new ReviewRequestDto(5L, 1L, 4.00, "ss");
        responseDto = new ReviewResponseDto(1L, 5L, 5L, 4.00, LocalDateTime.parse("2026-08-28T17:30:00"));
    }

    @Nested
    @DisplayName("POST /api/reviews")
    class CreateTests {

        @Test
        @DisplayName("Debe retornar HTTP 201 Created y el objeto creado")
        void create_ShouldReturnStatus201() throws Exception {
            when(reviewService.create(any(ReviewRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(post("/api/reviews")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.qualification").value(4))
                    .andExpect(jsonPath("$.hotelId").value(5));

            verify(reviewService).create(any(ReviewRequestDto.class));
        }
    }

    @Nested
    @DisplayName("GET /api/reviews")
    class FindAllTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 OK con la lista de reseñas")
        void findAll_ShouldReturnStatus200AndList() throws Exception {
            when(reviewService.findAll()).thenReturn(List.of(responseDto));

            mockMvc.perform(get("/api/reviews"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1));

            verify(reviewService).findAll();
        }
    }

    @Nested
    @DisplayName("GET /api/reviews/hotels")
    class FindByHotelTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 OK con la lista de reseñas para un hotel")
        void findByHotelId_WhenExists_ShouldReturnList() throws Exception {
            when(reviewService.findByHotelId(5L)).thenReturn(List.of(responseDto));

            mockMvc.perform(get("/api/reviews/hotel").param("hotelId", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1))
                    .andExpect(jsonPath("$[0].hotelId").value(5));

            verify(reviewService).findByHotelId(5L);
        }

        @Test
        @DisplayName("Debe retornar HTTP 200 OK y lista vacía si no hay reseñas")
        void findByHotelId_WhenNoReviews_ReturnsEmpty() throws Exception {
            when(reviewService.findByHotelId(99L)).thenReturn(List.of());

            mockMvc.perform(get("/api/reviews/hotel").param("hotelId", "99"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(0));

            verify(reviewService).findByHotelId(99L);
        }

    }

    @Nested
    @DisplayName("GET /api/reviews/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 OK cuando la reseña existe")
        void findById_WhenExists_ShouldReturnStatus200() throws Exception {
            when(reviewService.findById(1L)).thenReturn(responseDto);

            mockMvc.perform(get("/api/reviews/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(reviewService).findById(1L);
        }

        @Test
        @DisplayName("Debe retornar HTTP 404 Not Found si la reseña no existe")
        void findById_WhenDoesNotExist_ShouldReturnStatus404() throws Exception {
            when(reviewService.findById(99L)).thenThrow(new ReviewNotFoundException("Reseña no encontrada"));

            mockMvc.perform(get("/api/reviews/{id}", 99L))
                    .andExpect(status().isNotFound());

            verify(reviewService).findById(99L);
        }
    }

    @Nested
    @DisplayName("PUT /api/reviews/{id}")
    class UpdateTests {

        @Test
        @DisplayName("Debe retornar HTTP 200 OK al actualizar una reseña")
        void update_WhenExists_ShouldReturnStatus200() throws Exception {
            when(reviewService.update(eq(1L), any(ReviewRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(put("/api/reviews/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(reviewService).update(eq(1L), any(ReviewRequestDto.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/reviews/{id}")
    class DeleteTests {

        @Test
        @DisplayName("Debe retornar HTTP 204 No Content al eliminar")
        void delete_WhenExists_ShouldReturnStatus204() throws Exception {
            doNothing().when(reviewService).delete(1L);

            mockMvc.perform(delete("/api/reviews/{id}", 1L))
                    .andExpect(status().isNoContent());

            verify(reviewService).delete(1L);
        }

        @Test
        @DisplayName("Debe retornar HTTP 404 Not Found al intentar eliminar un id inexistente")
        void delete_WhenDoesNotExist_ShouldReturnStatus404() throws Exception {
            doThrow(new ReviewNotFoundException("No se encontró la reseña")).when(reviewService).delete(99L);

            mockMvc.perform(delete("/api/reviews/{id}", 99L))
                    .andExpect(status().isNotFound());

            verify(reviewService).delete(99L);
        }
    }
}