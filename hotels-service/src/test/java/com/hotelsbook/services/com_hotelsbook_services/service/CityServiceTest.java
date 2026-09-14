package com.hotelsbook.services.com_hotelsbook_services.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import com.hotelsbook.services.com_hotelsbook_services.dto.request.CityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.CityResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.entity.City;
import com.hotelsbook.services.com_hotelsbook_services.exception.EntityNotFoundException;
import com.hotelsbook.services.com_hotelsbook_services.mapper.CityMapper;
import com.hotelsbook.services.com_hotelsbook_services.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {
    @Mock
    private CityRepository cityRepository;

    @Mock
    private CityMapper cityMapper;

    @InjectMocks
    private CityService cityService;

    private City city;
    private CityRequestDto cityRequestDto;
    private CityResponseDto cityResponseDto;

    @BeforeEach
    void setUp() {
        city = new City("Madrid", "España");
        city.setId(1L);

        cityRequestDto = new CityRequestDto("Madrid", "España");
        cityResponseDto = new CityResponseDto(1L, "Madrid", "España");
    }

    @Nested
    @DisplayName("Método create()")
    class CreateTests {

        @Test
        @DisplayName("Debe crear una ciudad correctamente")
        void create_ShouldReturnCityDto() {

            when(cityMapper.toEntity(cityRequestDto)).thenReturn(city);
            when(cityRepository.save(city)).thenReturn(city);
            when(cityMapper.toResponseDto(city)).thenReturn(cityResponseDto);

            CityResponseDto result = cityService.create(cityRequestDto);

            assertEquals(result, cityResponseDto);
            assertNotNull(result);
            assertEquals(result.id(), cityResponseDto.id());
            assertEquals(result.name(), cityResponseDto.name());

            verify(cityMapper).toEntity(cityRequestDto);
            verify(cityRepository).save(city);
            verify(cityMapper).toResponseDto(city);
        }
    }

    @Nested
    @DisplayName("Método update()")
    class UpdateTests {

        @Test
        @DisplayName("Debe actualizar una ciudad si existe el id")
        void update_WhenIdExists_ShouldUpdateAndReturnResponse() {

            CityRequestDto updateDto = new CityRequestDto("ciudad editada", "pais editado");
            City updatedCity = new City("ciudad editada", "pais editado");
            updatedCity.setId(1L);
            CityResponseDto responseDto = new CityResponseDto(1L, "ciudad editada", "pais editado");

            when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
            when(cityRepository.save(city)).thenReturn(updatedCity);
            when(cityMapper.toResponseDto(updatedCity)).thenReturn(responseDto);

            // Act
            CityResponseDto result = cityService.update(1L, updateDto);

            assertEquals(responseDto, result);
            assertNotNull(result);
            assertEquals(responseDto.name(), result.name());

            verify(cityRepository).findById(1L);
            verify(cityRepository).save(city);
        }

        @Test
        @DisplayName("Debe lanzar excepción si el id no existe")
        void update_WhenIdDoesNotExist_ShouldThrowException() {
            Long idInexistente = 999L;

            when(cityRepository.findById(idInexistente)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> cityService.update(idInexistente, cityRequestDto));

            assertEquals("Ciudad un encontrada con el id: 999", exception.getMessage());

            verify(cityRepository).findById(idInexistente);
        }
    }

    @Nested
    @DisplayName("Método findById()")
    class FindByIdTests {

        @Test
        @DisplayName("Debe retornar la ciudad cuando existe el ID")
        void findById_WhenIdExists_ShouldReturnCity() {
            // Arrange
            when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
            when(cityMapper.toResponseDto(city)).thenReturn(cityResponseDto);

            // Act
            CityResponseDto result = cityService.findById(1L);

            // Assert
            assertNotNull(result);
            verify(cityRepository).findById(1L);
        }

        @Test
        @DisplayName("Debe lanzar EntityNotFoundException cuando no existe el ID")
        void findById_WhenIdDoesNotExist_ShouldThrowException() {

            Long idInexistente = 999L;

            // Arrange
            when(cityRepository.findById(999L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> cityService.findById(idInexistente));

            assertEquals("Ciudad un encontrada con el id: 999", exception.getMessage());

            verify(cityRepository).findById(999L);
            verify(cityMapper, never()).toResponseDto(any());
        }
    }

    @Nested
    @DisplayName("Método deleteById()")
    class DeleteTests {

        @Test
        @DisplayName("Debe eliminar la ciudad si existe")
        void deleteById_WhenIdExists_ShouldDeleteCity() {
            // Arrange
            when(cityRepository.findById(1L)).thenReturn(Optional.of(city));

            // Act
            cityService.deleteById(1L);

            // Assert
            verify(cityRepository).findById(1L);
            verify(cityRepository).delete(city);
        }

        @Test
        @DisplayName("Debe lanzar excepción al intentar eliminar si el ID no existe")
        void deleteById_WhenIdDoesNotExist_ShouldThrowException() {

            Long idInexistente = 999L;

            // Arrange
            when(cityRepository.findById(idInexistente)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> cityService.findById(idInexistente));

            assertEquals("Ciudad un encontrada con el id: 999", exception.getMessage());

            verify(cityRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Método findAll()")
    class FindAllTests {

        @Test
        @DisplayName("Debe retornar la lista de ciudades cuando existen registros")
        void findAll_WhenCitiesExist_ShouldReturnCityList() {
            // Arrange
            List<City> cities = List.of(city);
            Pageable pageable = PageRequest.of(0, 10);
            Page<City> cityPage = new PageImpl<>(cities, pageable, cities.size());
            when(cityRepository.findAll(pageable)).thenReturn(cityPage);
            when(cityMapper.toResponseDto(city)).thenReturn(cityResponseDto);

            // Act
            Page<CityResponseDto> result = cityService.findAll(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals("Madrid", result.getContent().get(0).name());

            verify(cityRepository).findAll(pageable);
            verify(cityMapper).toResponseDto(city);
        }

        @Test
        @DisplayName("Debe retornar una lista vacía cuando no existen registros")
        void findAll_WhenNoCitiesExist_ShouldReturnEmptyList() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            when(cityRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

            // Act
            Page<CityResponseDto> result = cityService.findAll(pageable);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(cityRepository).findAll(pageable);
            verify(cityMapper, never()).toResponseDto(any());
        }
    }

}
