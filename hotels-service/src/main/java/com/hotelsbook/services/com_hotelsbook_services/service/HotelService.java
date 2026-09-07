package com.hotelsbook.services.com_hotelsbook_services.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.*;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.*;
import com.hotelsbook.services.com_hotelsbook_services.entity.*;
import com.hotelsbook.services.com_hotelsbook_services.exception.EntityNotFoundException;
import com.hotelsbook.services.com_hotelsbook_services.mapper.*;
import com.hotelsbook.services.com_hotelsbook_services.repository.*;

/**
 * Servicio encargado de gestionar la lógica de negocio relativa a los hoteles.
 * Implementa la interfaz genérica CrudService.
 */
@Service
@Transactional
public class HotelService implements CrudService<HotelRequestDto, HotelResponseDto> {

    private final HotelMapper mapper;
    private final AddressMapper addressMapper;
    private final HotelRepository repository;
    private final RoomTypeMapper roomTypeMapper;
    private final AmenityRepository amenityRepository;
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    private static final String HOTEL_NOT_FOUND = "Hotel no encontrado con el id indicado";

    /**
     * Constructor para la inyección de dependencias.
     * 
     * @param mapper            Mapeador para transformar entre entidades Hotel y
     *                          sus DTOs
     * @param repository        Repositorio de persistencia de datos para Hotel
     * @param addressMapper     Mapeador para transformar objetos Address
     * @param amenityRepository Repositorio de persistencia de datos para Amenity
     * @param roomTypeMapper    Mapeador para transformar objetos RoomType
     */
    public HotelService(
            HotelMapper mapper,
            HotelRepository repository,
            AddressMapper addressMapper,
            AmenityRepository amenityRepository,
            RoomTypeMapper roomTypeMapper,
            CityRepository cityRepository,
            CityMapper cityMapper) {
        this.mapper = mapper;
        this.repository = repository;
        this.addressMapper = addressMapper;
        this.amenityRepository = amenityRepository;
        this.roomTypeMapper = roomTypeMapper;
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
    }

    /**
     * Crea un nuevo registro de hotel en la base de datos a partir de la
     * información recibida.
     * 
     * @param request Objeto DTO que contiene los datos del hotel a crear
     *                (HotelRequestDto)
     * @return Objeto DTO con los datos del hotel guardado y su ID generado
     *         (HotelResponseDto)
     */
    @Override
    public HotelResponseDto create(HotelRequestDto request) {
        Hotel hotel = mapper.toEntity(request);

        City city = cityRepository.findByName(request.address().city().name())
                .orElseGet(() -> cityRepository.save(cityMapper.toEntity(request.address().city())));

        hotel.getAddress().setCity(city);

        // Si se reciben IDs de amenities, se recuperan las entidades de la BD y se
        // asocian al hotel
        if (request.amenitiesIds() != null && !request.amenitiesIds().isEmpty()) {
            Set<Amenity> amenities = amenityRepository.findAllById(request.amenitiesIds())
                    .stream()
                    .collect(Collectors.toSet());

            hotel.setAmenities(amenities);
        }

        Hotel saved = repository.save(hotel);
        return mapper.toResponseDto(saved);
    }

    /**
     * Elimina un registro de hotel existente en la base de datos según su
     * identificador.
     * 
     * @param id Identificador único del hotel a eliminar (Long)
     * @throws EntityNotFoundException Si el hotel con el ID especificado no existe
     *                                 en la BD
     */
    @Override
    public void deleteById(Long id) {
        boolean existHotel = repository.existsById(id);

        if (existHotel) {
            repository.deleteById(id);
        } else {
            throw new EntityNotFoundException(HOTEL_NOT_FOUND);
        }
    }

    /**
     * Obtiene el listado completo de todos los hoteles registrados.
     * 
     * @return Lista de objetos DTO con la información de cada hotel
     *         (List<HotelResponseDto>)
     */
    @Override
    public List<HotelResponseDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    /**
     * Busca un hotel específico a través de su identificador único.
     * 
     * @param id Identificador único del hotel (Long)
     * @return Objeto DTO con la información del hotel encontrado (HotelResponseDto)
     * @throws EntityNotFoundException Si no se encuentra ningún hotel con el ID
     *                                 especificado
     */
    @Override
    public HotelResponseDto findById(Long id) {
        Hotel hotel = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HOTEL_NOT_FOUND));
        return mapper.toResponseDto(hotel);
    }

    /**
     * Actualiza los datos de un hotel existente en la base de datos.
     * 
     * @param id      Identificador único del hotel que se desea actualizar (Long)
     * @param request Objeto DTO con los datos actualizados (HotelRequestDto)
     * @return Objeto DTO con la información del hotel tras ser guardado
     *         (HotelResponseDto)
     * @throws EntityNotFoundException Si el hotel a actualizar no existe en la BD
     */
    @Override
    public HotelResponseDto update(Long id, HotelRequestDto request) {
        Hotel hotel = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HOTEL_NOT_FOUND));

        hotel.setName(request.name());
        hotel.setDescription(request.description());
        hotel.setCapacity(request.capacity());
        hotel.setStars(request.stars());

        // Actualización opcional de la dirección (Address)
        if (request.address() != null) {
            City city = cityRepository.findByName(request.address().city().name())
                    .orElseGet(() -> cityRepository.save(cityMapper.toEntity(request.address().city())));

            hotel.getAddress().setCity(city);
            Address newAddress = addressMapper.toEntity(request.address());
            hotel.setAddress(newAddress);
        }

        // Actualización opcional de los tipos de habitación (RoomTypes)
        if (request.roomTypes() != null && !request.roomTypes().isEmpty()) {
            Set<RoomType> newRoomTypes = request.roomTypes()
                    .stream()
                    .map(roomTypeMapper::toEntity)
                    .collect(Collectors.toSet());
            hotel.setRoomTypes(newRoomTypes);
        }

        // Actualización opcional de los servicios (Amenities) por sus IDs
        if (request.amenitiesIds() != null && !request.amenitiesIds().isEmpty()) {
            Set<Amenity> amenities = amenityRepository.findAllById(request.amenitiesIds())
                    .stream()
                    .collect(Collectors.toSet());

            hotel.setAmenities(amenities);
        }

        Hotel updated = repository.save(hotel);

        return mapper.toResponseDto(updated);
    }
}
