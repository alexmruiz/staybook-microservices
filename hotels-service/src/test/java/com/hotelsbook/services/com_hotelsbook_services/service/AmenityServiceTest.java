package com.hotelsbook.services.com_hotelsbook_services.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.AmenityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.AmenityResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.entity.Amenity;
import com.hotelsbook.services.com_hotelsbook_services.exception.EntityNotFoundException;
import com.hotelsbook.services.com_hotelsbook_services.mapper.AmenityMapper;
import com.hotelsbook.services.com_hotelsbook_services.repository.AmenityRepository;

@ExtendWith(MockitoExtension.class)
class AmenityServiceTest {

    @Mock
    private AmenityRepository repository;

    @Mock
    private AmenityMapper mapper;

    @InjectMocks
    private AmenityService service;

    private Amenity amenity;

    private AmenityRequestDto request;

    private AmenityResponseDto response;

    @BeforeEach
    void setUp() {
        request = new AmenityRequestDto("Wi-Fi", "Conexión de alta velocidad");

        amenity = new Amenity("Wi-Fi", "Conexión de alta velocidad");
        response = new AmenityResponseDto(1L, "Wi-Fi", "Conexión de alta velocidad");
    }

    @Test
    @DisplayName("Debe crear un servicio")
    void create_ShouldReturnAmenityResponseDto() {
        when(mapper.toEntity(request)).thenReturn(amenity);
        when(repository.save(amenity)).thenReturn(amenity);
        when(mapper.toResponseDto(amenity)).thenReturn(response);

        AmenityResponseDto result = service.create(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Wi-Fi", result.name());

        verify(mapper).toEntity(request);
        verify(repository).save(amenity);
        verify(mapper).toResponseDto(amenity);
    }

    @Test
    void findAll_shoulReturnList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Amenity> amenityPage = new PageImpl<>(List.of(amenity), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(amenityPage);
        when(mapper.toResponseDto(amenity)).thenReturn(response);

        Page<AmenityResponseDto> result = service.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Wi-Fi", result.getContent().get(0).name());

        verify(repository).findAll(pageable);
        verify(mapper).toResponseDto(amenity);
    }

    @Test
    void findAll_ReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<AmenityResponseDto> result = service.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findAll(pageable);
        verify(mapper, never()).toResponseDto(any());
    }

    @Test
    void findById_ShouldReturnAmenity() {
        when(repository.findById(1L)).thenReturn(Optional.of(amenity));
        when(mapper.toResponseDto(amenity)).thenReturn(response);

        AmenityResponseDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());

        verify(repository).findById(1L);
        verify(mapper).toResponseDto(amenity);

    }

    @Test
    void findById_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> service.findById(99L));

        assertEquals("Servicio no encontrado con id: 99", exception.getMessage());

        verify(repository).findById(99L);
        verify(mapper, never()).toResponseDto(any());
    }

    @Test
    void update_WhenIdExists_ShouldReturnUpdatedAmenityResponseDto() {
        // Arange
        Long id = 1L;
        AmenityRequestDto updateDto = new AmenityRequestDto("Wi-Fi", "Wi-Fi de alta velocidad");
        Amenity updAmenity = new Amenity("Wi-Fi", "Wi-Fi de alta velocidad");

        AmenityResponseDto updatedResponse = new AmenityResponseDto(1L, "Wi-Fi", "Wi-Fi de alta velocidad");

        when(repository.findById(id)).thenReturn(Optional.of(amenity));
        when(repository.save(amenity)).thenReturn(updAmenity);
        when(mapper.toResponseDto(updAmenity)).thenReturn(updatedResponse);

        // Act
        AmenityResponseDto result = service.update(id, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Wi-Fi", result.name());

        verify(repository).findById(id);
        verify(repository).save(amenity);
        verify(mapper).toResponseDto(updAmenity);
    }

    @Test
    void update_WhenIdDoesNotExist_ShouldThorwException() {
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> service.update(id, request));

        assertEquals("Servicio no encontrado con id: 99", exception.getMessage());
        verify(repository).findById(id);
        verify(repository, never()).save(any());
        verify(mapper, never()).toResponseDto(any());
    }

    @Test
    void deleteById_WhenIdExist_ShoulDeleteAmenity() {
        // Arrange
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);
        doNothing().when(repository).deleteById(id);

        // Act
        service.deleteById(id);

        // Assert
        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    void deleteById_WhenIdDoesNotExist_ShouldThrowException() {
        // Arrange
        Long id = 99L;
        when(repository.existsById(id)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> service.deleteById(id));

        assertEquals("Servicio no encontrado con id: 99", exception.getMessage());
        verify(repository).existsById(id);
        verify(repository, never()).deleteById(anyLong());

    }

}
