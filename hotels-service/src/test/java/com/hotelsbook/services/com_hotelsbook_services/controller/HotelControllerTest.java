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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelsbook.services.com_hotelsbook_services.dto.request.AddressRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.request.CityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.request.HotelRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.request.RoomTypeRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.HotelResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.RoomTypeResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.AmenityResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.entity.RoomTypeName;
import com.hotelsbook.services.com_hotelsbook_services.service.HotelService;

@WebMvcTest(HotelController.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HotelService service;

    private HotelRequestDto request;
    private HotelResponseDto response;

    @BeforeEach
    void setUp() {
        CityRequestDto cityRequestDto = new CityRequestDto("Madrid", "España");
        AddressRequestDto addressRequestDto = new AddressRequestDto("Gran Vía", "12", "12345", cityRequestDto);

        RoomTypeRequestDto roomTypeRequestDto = new RoomTypeRequestDto(RoomTypeName.DOUBLE, 20);

        request = new HotelRequestDto(
                "Hotel Gran Vía",
                "Descripción",
                addressRequestDto,
                5,
                100,
                Set.of(roomTypeRequestDto),
                Set.of(1L));

        response = new HotelResponseDto(
                1L,
                "Hotel Gran Vía",
                "Descripción",
                5,
                100,
                Set.of(new RoomTypeResponseDto(1L, RoomTypeName.DOUBLE, 20)),
                Set.of(new AmenityResponseDto(1L, "Wi-Fi", "Alta velocidad")));
    }

    @Test
    void create_WhenValidRequest_ShouldReturn201Created() throws Exception {
        when(service.create(any(HotelRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hotel Gran Vía"))
                .andExpect(jsonPath("$.description").value("Descripción"))
                .andExpect(jsonPath("$.stars").value(5))
                .andExpect(jsonPath("$.capacity").value(100));

        verify(service).create(any(HotelRequestDto.class));
    }

    @Test
    void findAll_ShouldReturn200OkAndList() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<HotelResponseDto> page = new PageImpl<>(List.of(response), pageable, 1);
        when(service.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Hotel Gran Vía"));

        verify(service).findAll(any(Pageable.class));
    }

    @Test
    void findById_WhenExists_ShouldReturn200Ok() throws Exception {
        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/hotels/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hotel Gran Vía"));

        verify(service).findById(1L);
    }

    @Test
    void update_WhenValidRequest_ShouldReturn200Ok() throws Exception {
        when(service.update(eq(1L), any(HotelRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/hotels/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hotel Gran Vía"));

        verify(service).update(eq(1L), any(HotelRequestDto.class));
    }

    @Test
    void delete_WhenCalled_ShouldReturn204NoContent() throws Exception {
        doNothing().when(service).deleteById(1L);

        mockMvc.perform(delete("/api/hotels/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).deleteById(1L);
    }
}