package com.hotelsbook.services.com_hotelsbook_services.controller;

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
import com.hotelsbook.services.com_hotelsbook_services.dto.request.AmenityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.AmenityResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.exception.EntityNotFoundException;
import com.hotelsbook.services.com_hotelsbook_services.service.AmenityService;

@WebMvcTest(AmenityController.class)
class AmenityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AmenityService service;

    private AmenityRequestDto request;
    private AmenityResponseDto response;

    @BeforeEach
    void setUp() {
        request = new AmenityRequestDto("Wi-Fi", "Conexión de alta velocidad");
        response = new AmenityResponseDto(1L, "Wi-Fi", "Conexión de alta velocidad");
    }

    @Test
    void create_WhenValidRequest_ShouldReturn201Created() throws Exception {
        when(service.create(any(AmenityRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/amenities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Wi-Fi"))
                .andExpect(jsonPath("$.description").value("Conexión de alta velocidad"));

        verify(service).create(any(AmenityRequestDto.class));
    }

    @Test
    void findAll_ShouldReturn200OkAndList() throws Exception {
        when(service.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Wi-Fi"));

        verify(service).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturn200Ok() throws Exception {
        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/amenities/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Wi-Fi"));

        verify(service).findById(1L);
    }

    @Test
    void findById_WhenNotFound_ShouldReturn404() throws Exception {
        when(service.findById(99L)).thenThrow(new EntityNotFoundException("Servicio no encontrado con id: 99"));

        mockMvc.perform(get("/api/amenities/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(service).findById(99L);
    }

    @Test
    void update_WhenValidRequest_ShouldReturn200Ok() throws Exception {
        when(service.update(eq(1L), any(AmenityRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/amenities/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Wi-Fi"));

        verify(service).update(eq(1L), any(AmenityRequestDto.class));
    }

    @Nested 
    @DisplayName("DELETE /api/amenities/{id}")
    class DeleteTests {

        @Test
        @DisplayName("Debe responder con estado 204 No Content al eliminar un servicio")
        void delete_WhenExists_ShouldReturn204NoContent() throws Exception {
            doNothing().when(service).deleteById(1L);

            mockMvc.perform(delete("/api/amenities/{id}", 1L))
                    .andExpect(status().isNoContent());

            verify(service).deleteById(1L);
        }

        @Test
        @DisplayName("Debe responder con estado 404 Not Found al intentar eliminar un id inexistente")
        void delete_WhenNotFound_ShouldReturn404() throws Exception {
            doThrow(new EntityNotFoundException("Servicio no encontrado con id: 99")).when(service).deleteById(99L);

            mockMvc.perform(delete("/api/amenities/{id}", 99L))
                    .andExpect(status().isNotFound());

            verify(service).deleteById(99L);
        }
    }

}
