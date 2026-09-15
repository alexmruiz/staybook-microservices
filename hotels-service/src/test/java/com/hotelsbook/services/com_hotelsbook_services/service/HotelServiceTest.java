package com.hotelsbook.services.com_hotelsbook_services.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import com.hotelsbook.services.com_hotelsbook_services.dto.request.AddressRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.request.CityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.request.HotelRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.request.RoomTypeRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.HotelResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.entity.Address;
import com.hotelsbook.services.com_hotelsbook_services.entity.Amenity;
import com.hotelsbook.services.com_hotelsbook_services.entity.City;
import com.hotelsbook.services.com_hotelsbook_services.entity.Hotel;
import com.hotelsbook.services.com_hotelsbook_services.entity.RoomType;
import com.hotelsbook.services.com_hotelsbook_services.entity.RoomTypeName;
import com.hotelsbook.services.com_hotelsbook_services.exception.EntityNotFoundException;
import com.hotelsbook.services.com_hotelsbook_services.mapper.CityMapper;
import com.hotelsbook.services.com_hotelsbook_services.mapper.HotelMapper;
import com.hotelsbook.services.com_hotelsbook_services.mapper.RoomTypeMapper;
import com.hotelsbook.services.com_hotelsbook_services.repository.AmenityRepository;
import com.hotelsbook.services.com_hotelsbook_services.repository.CityRepository;
import com.hotelsbook.services.com_hotelsbook_services.repository.HotelRepository;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelMapper mapper;
    @Mock
    private HotelRepository repository;
    @Mock
    private AmenityRepository amenityRepository;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private CityMapper cityMapper;

    @Mock
    private RoomTypeMapper roomTypeMapper;

    @InjectMocks
    private HotelService service;

    private Hotel hotel;
    private HotelRequestDto request;
    private HotelResponseDto response;
    private City city;

    @BeforeEach
    void setUp() {
        CityRequestDto cityRequestDto = new CityRequestDto("Madrid", "España");
        city = new City("Madrid", "España");

        RoomTypeRequestDto roomTypeRequestDto = new RoomTypeRequestDto(RoomTypeName.DOUBLE, 20);
        Set<RoomTypeRequestDto> roomTypes = new HashSet<>();
        roomTypes.add(roomTypeRequestDto);

        AddressRequestDto addressRequestDto = new AddressRequestDto("Gran Vía", "12", "12345", cityRequestDto);

        Set<Long> amenitiesIds = Set.of(1L);

        request = new HotelRequestDto(
                "Hotel Gran Vía",
                "Descripción",
                addressRequestDto,
                5,
                100,
                roomTypes,
                amenitiesIds);

        Address address = new Address("Gran Vía", "12", "12345", city);
        hotel = new Hotel("Hotel Gran Vía", "Descripción", address, 5, 100);

        response = new HotelResponseDto(
                1L,
                "Hotel Gran Vía",
                "Descripción",
                5,
                100,
                Set.of(),
                Set.of());
    }

    @Nested
    @DisplayName("Test para create()")
    class CreateTeste {
        @Test
        void create_WhenCityExistsAndAmenitiesProvided_ShoulReturnResponseDto() {
            // Arrange
            Amenity amenity = new Amenity("Wi-Fi", "Wi-Fi de alta velocidad");

            when(mapper.toEntity(request)).thenReturn(hotel);
            when(cityRepository.findByName("Madrid")).thenReturn(Optional.of(city));
            when(amenityRepository.findAllById(Set.of(1L))).thenReturn(List.of(amenity));
            when(repository.save(hotel)).thenReturn(hotel);
            when(mapper.toResponseDto(hotel)).thenReturn(response);

            // Act
            HotelResponseDto result = service.create(request);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("Hotel Gran Vía", result.name());

            // Verify
            verify(cityRepository).findByName("Madrid");
            verify(cityRepository, never()).save(any());
            verify(amenityRepository).findAllById(Set.of(1L));
            verify(repository).save(hotel);
        }

        @Test
        void create_WhenCityDoesNotExist_ShouldSaveNewCity() {
            // Arrange
            when(mapper.toEntity(request)).thenReturn(hotel);
            when(cityRepository.findByName("Madrid")).thenReturn(Optional.empty());
            when(cityMapper.toEntity(any())).thenReturn(city);
            when(amenityRepository.findAllById(Set.of(1L))).thenReturn(List.of());
            when(repository.save(hotel)).thenReturn(hotel);
            when(mapper.toResponseDto(hotel)).thenReturn(response);

            // Act
            HotelResponseDto result = service.create(request);

            // Asset
            assertNotNull(result);
            verify(cityRepository).findByName("Madrid");
            verify(cityRepository).save(city);
            verify(repository).save(hotel);
        }

    }

    @Nested
    @DisplayName("Test para Delete()")
    class DeleteByIdTest {

        @Test
        void deleteById_whenHotelExists_ShouldDelete() {
            // Arrange
            Long hotelId = 1L;
            when(repository.existsById(hotelId)).thenReturn(true);
            doNothing().when(repository).deleteById(hotelId);

            // Act
            assertDoesNotThrow(() -> service.deleteById(hotelId));

            // verify
            verify(repository).existsById(hotelId);
            verify(repository).deleteById(hotelId);
        }

        @Test
        void deleteById_WhenHotelDoesNotExists_ShoulThrowException() {
            // Arrange
            Long id = 99L;

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> service.deleteById(id));

            assertEquals("Hotel no encontrado con el id indicado", exception.getMessage());
            verify(repository).existsById(id);
            verify(repository, never()).deleteById(anyLong());

        }
    }

    @Nested
    @DisplayName("Test para findAll()")
    class FindAllTest {

        @Test
        void findAll_WhenHotelsExist_ShouldReturnHotelList() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Hotel> hotelPage = new PageImpl<>(List.of(hotel), pageable, 1);
            when(repository.findAll(pageable)).thenReturn(hotelPage);
            when(mapper.toResponseDto(hotel)).thenReturn(response);

            // Act
            Page<HotelResponseDto> result = service.findAll(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals("Hotel Gran Vía", result.getContent().get(0).name());

            verify(repository).findAll(pageable);
            verify(mapper).toResponseDto(hotel);
        }

        @Test
        void findAll_WhenNoHotelExist_ShouldReturnEmptyList() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            when(repository.findAll(pageable)).thenReturn(Page.empty(pageable));

            // Act
            Page<HotelResponseDto> result = service.findAll(pageable);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(repository).findAll(pageable);
            verify(mapper, never()).toResponseDto(any());
        }
    }

    @Nested
    @DisplayName("Test para findById()")
    class FindById {

        @Test
        void findById_WhenIdExists_ShouldReturnHotelResponseDto() {
            // Arrange
            Long hotelId = 1L;
            when(repository.findById(hotelId)).thenReturn(Optional.of(hotel));
            when(mapper.toResponseDto(hotel)).thenReturn(response);

            // Act
            HotelResponseDto result = service.findById(hotelId);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("Hotel Gran Vía", result.name());

            verify(repository).findById(hotelId);
            verify(mapper).toResponseDto(hotel);
        }

        @Test
        void findById_WhenIdDoesNotExist_ShouldThrowException() {
            // Arrange
            Long hotelId = 99L;
            when(repository.findById(hotelId)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> service.findById(hotelId));

            assertEquals("Hotel no encontrado con el id indicado", exception.getMessage());
            verify(repository).findById(hotelId);
            verify(mapper, never()).toResponseDto(any()); // Certifica que el mapper no llegó a ejecutarse
        }
    }

    @Nested
    @DisplayName("Test para update()")
    class UpdateTest {
        @Test
        void update_WhenHotelExists_ShouldUpdateAndReturnResponseDto() {
            Long hotelId = 1L;

            RoomType existingRoom = new RoomType(RoomTypeName.DOUBLE, 2, hotel);
            hotel.addRoomType(existingRoom);

            RoomTypeRequestDto updateRoomDto = new RoomTypeRequestDto(RoomTypeName.DOUBLE, 5);
            RoomTypeRequestDto newRoomDto = new RoomTypeRequestDto(RoomTypeName.SINGLE, 3);

            Set<RoomTypeRequestDto> updatedRoomTypes = new HashSet<>();
            updatedRoomTypes.add(updateRoomDto);
            updatedRoomTypes.add(newRoomDto);

            AddressRequestDto updatedAddress = new AddressRequestDto(
                    "Gran Vía",
                    "12",
                    "12345",
                    new CityRequestDto("Madrid", "España"));

            HotelRequestDto updateRequest = new HotelRequestDto(
                    "Hotel Vía Actualizado",
                    "Nueva Descripción",
                    updatedAddress,
                    5,
                    200,
                    updatedRoomTypes,
                    Set.of(1L));

            when(repository.findById(hotelId)).thenReturn(Optional.of(hotel));
            when(cityRepository.findByName("Madrid")).thenReturn(Optional.of(city));
            when(roomTypeMapper.toEntity(any(RoomTypeRequestDto.class)))
                    .thenAnswer(inv -> {
                        RoomTypeRequestDto dto = inv.getArgument(0);
                        return new RoomType(dto.type(), dto.quantity(), hotel);
                    });
            when(amenityRepository.findAllById(Set.of(1L)))
                    .thenReturn(List.of(new Amenity("Wi-Fi", "Wi-Fi de alta velocidad")));
            when(repository.save(hotel)).thenReturn(hotel);
            when(mapper.toResponseDto(hotel)).thenReturn(response);

            HotelResponseDto result = service.update(hotelId, updateRequest);

            assertNotNull(result);
            assertEquals("Hotel Gran Vía", result.name());

            verify(repository).findById(hotelId);
            verify(cityRepository).findByName("Madrid");
            verify(roomTypeMapper).toEntity(newRoomDto);
            verify(amenityRepository).findAllById(Set.of(1L));
            verify(repository).save(hotel);
            verify(mapper).toResponseDto(hotel);
        }

        @Test
        void update_WhenOptionalFieldsAreNullOrEmpty_ShouldUpdateBasicFieldsOnly() {
            // Arrange
            Long hotelId = 1L;

            HotelRequestDto simpleRequest = new HotelRequestDto(
                    "Nombre Simple",
                    "Desc Simple",
                    null,
                    3,
                    50,
                    null,
                    null);

            when(repository.findById(hotelId)).thenReturn(Optional.of(hotel));
            when(repository.save(hotel)).thenReturn(hotel);
            when(mapper.toResponseDto(hotel)).thenReturn(response);

            // Act
            HotelResponseDto result = service.update(hotelId, simpleRequest);

            // Assert
            assertNotNull(result);
            assertEquals("Nombre Simple", hotel.getName());

            // Verificamos que jamás se interactuó con los repositorios opcionales
            verify(cityRepository, never()).findByName(any());
            verify(roomTypeMapper, never()).toEntity(any());
            verify(amenityRepository, never()).findAllById(any());
            verify(repository).save(hotel);
        }

        @Test
        @DisplayName("Debe lanzar EntityNotFoundException cuando el ID no existe")
        void update_WhenHotelDoesNotExist_ShouldThrowException() {
            // Arrange
            Long hotelId = 99L;
            when(repository.findById(hotelId)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> service.update(hotelId, request));

            assertEquals("Hotel no encontrado con el id indicado", exception.getMessage());
            verify(repository).findById(hotelId);
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Test para findByCity()")
    class FindByCityTest {

        @Test
        void findByCity_WhenCityExistsAndHotelsExist_ShouldReturnHotelList() {
            Long cityId = 1L;

            when(cityRepository.existsById(cityId)).thenReturn(true);
            when(repository.findByAddressCityId(cityId)).thenReturn(List.of(hotel));
            when(mapper.toResponseDto(hotel)).thenReturn(response);

            List<HotelResponseDto> result = service.findByCity(cityId);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Hotel Gran Vía", result.get(0).name());

            verify(cityRepository).existsById(cityId);
            verify(repository).findByAddressCityId(cityId);
            verify(mapper).toResponseDto(hotel);
        }

        @Test
        void findByCity_WhenCityExistsAndNoHotelsExist_ShouldReturnEmptyList() {
            Long cityId = 1L;

            when(cityRepository.existsById(cityId)).thenReturn(true);
            when(repository.findByAddressCityId(cityId)).thenReturn(List.of());

            List<HotelResponseDto> result = service.findByCity(cityId);

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(cityRepository).existsById(cityId);
            verify(repository).findByAddressCityId(cityId);
            verify(mapper, never()).toResponseDto(any());
        }

        @Test
        void findByCity_WhenCityDoesNotExist_ShouldThrowException() {
            Long cityId = 99L;

            when(cityRepository.existsById(cityId)).thenReturn(false);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> service.findByCity(cityId));

            assertEquals("Hotel no encontrado con el id indicado", exception.getMessage());

            verify(cityRepository).existsById(cityId);
            verify(repository, never()).findByAddressCityId(anyLong());
            verify(mapper, never()).toResponseDto(any());
        }
    }

}
