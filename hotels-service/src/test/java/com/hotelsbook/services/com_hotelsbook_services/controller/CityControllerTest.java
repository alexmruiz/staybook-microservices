package com.hotelsbook.services.com_hotelsbook_services.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

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
import com.hotelsbook.services.com_hotelsbook_services.dto.request.CityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.CityResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.HotelResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.exception.EntityNotFoundException;
import com.hotelsbook.services.com_hotelsbook_services.service.CityService;
import com.hotelsbook.services.com_hotelsbook_services.service.HotelService;

@WebMvcTest(CityController.class)
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CityService cityService;

    @MockitoBean
    private HotelService hotelService;

    private CityRequestDto requestDto;
    private CityResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new CityRequestDto("Madrid", "España");
        responseDto = new CityResponseDto(1L, "Madrid", "España");
    }

    @Nested
    @DisplayName("POST /api/cities")
    class CreateTests {

        @Test
        @DisplayName("Debe devolver status 201 Created al crear una ciudad válida")
        void create_WhenValidRequest_ShouldReturn201Created() throws Exception {
            when(cityService.create(any(CityRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(post("/api/cities")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Madrid"))
                    .andExpect(jsonPath("$.country").value("España"));

            verify(cityService).create(any(CityRequestDto.class));
        }
    }

    @Nested
    @DisplayName("GET /api/cities")
    class FindAllTests {

        @Test
        @DisplayName("Debe devolver status 200 OK y la lista de ciudades")
        void findAll_ShouldReturn200OkAndList() throws Exception {
            when(cityService.findAll()).thenReturn(List.of(responseDto));

            mockMvc.perform(get("/api/cities"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("Madrid"));

            verify(cityService).findAll();
        }
    }

    @Nested
    @DisplayName("GET /api/cities/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Debe devolver status 200 OK si la ciudad existe")
        void findById_WhenExists_ShouldReturn200Ok() throws Exception {
            when(cityService.findById(1L)).thenReturn(responseDto);

            mockMvc.perform(get("/api/cities/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Madrid"));

            verify(cityService).findById(1L);
        }

        @Test
        @DisplayName("Debe devolver el status configurado por la excepción si no existe")
        void findById_WhenNotFound_ShouldReturnError() throws Exception {
            when(cityService.findById(99L)).thenThrow(new EntityNotFoundException("Ciudad no encontrada"));

            mockMvc.perform(get("/api/cities/{id}", 99L))
                    .andExpect(status().isNotFound());

            verify(cityService).findById(99L);
        }
    }

    @Nested
    @DisplayName("PUT /api/cities/{id}")
    class UpdateTests {

        @Test
        @DisplayName("Debe devolver status 200 OK al actualizar correctamente")
        void update_WhenValid_ShouldReturn200Ok() throws Exception {
            when(cityService.update(eq(1L), any(CityRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(put("/api/cities/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Madrid"));

            verify(cityService).update(eq(1L), any(CityRequestDto.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/cities/{id}")
    class DeleteTests {

        @Test
        @DisplayName("Debe devolver status 204 No Content al eliminar con éxito")
        void delete_WhenExists_ShouldReturn204NoContent() throws Exception {
            doNothing().when(cityService).deleteById(1L);

            mockMvc.perform(delete("/api/cities/{id}", 1L))
                    .andExpect(status().isNoContent());

            verify(cityService).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/cities/{cityId}/hotels")
    class FindHotelsByCityTests {

        @Test
        @DisplayName("Debe devolver status 200 OK y la lista de hoteles de la ciudad")
        void findHotelsByCity_ShouldReturn200Ok() throws Exception {
            HotelResponseDto hotelResponse = new HotelResponseDto(
                    10L,
                    "Gran Hotel",
                    "Calle Mayor 1",
                    5,
                    40,
                    Set.of(),
                    Set.of());

            when(hotelService.findByCity(1L)).thenReturn(List.of(hotelResponse));

            mockMvc.perform(get("/api/cities/{cityId}/hotels", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1))
                    .andExpect(jsonPath("$[0].id").value(10))
                    .andExpect(jsonPath("$[0].name").value("Gran Hotel"))
                    .andExpect(jsonPath("$[0].description").value("Calle Mayor 1"));

            verify(hotelService).findByCity(1L);
        }
    }
}
