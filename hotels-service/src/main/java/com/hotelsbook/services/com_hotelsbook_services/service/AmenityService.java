package com.hotelsbook.services.com_hotelsbook_services.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.AmenityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.AmenityResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.entity.Amenity;
import com.hotelsbook.services.com_hotelsbook_services.exception.EntityNotFoundException;
import com.hotelsbook.services.com_hotelsbook_services.mapper.AmenityMapper;
import com.hotelsbook.services.com_hotelsbook_services.repository.AmenityRepository;

@Service
public class AmenityService implements CrudService<AmenityRequestDto, AmenityResponseDto> {

    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    private static final String NOT_FOUND_MESSAGE = "Servicio no encontrado con id: ";

    public AmenityService(AmenityRepository amenityRepository, AmenityMapper amenityMapper) {
        this.amenityRepository = amenityRepository;
        this.amenityMapper = amenityMapper;
    }

    /**
     * Create an Amenity
     * 
     * @param request
     * @return AmenityResponseDto
     */
    @Override
    public AmenityResponseDto create(AmenityRequestDto request) {
        Amenity amenity = amenityMapper.toEntity(request);
        Amenity saved = amenityRepository.save(amenity);
        return amenityMapper.toResponseDto(saved);
    }

    /**
     * Find all amenities paginated
     * 
     * @param pageable
     * @return Page<AmenityResponseDto>
     */
    @Override
    public Page<AmenityResponseDto> findAll(Pageable pageable) {
        return amenityRepository.findAll(pageable)
                .map(amenityMapper::toResponseDto);
    }

    /**
     * Find an Amenity by id
     * 
     * @param id
     * @return AmenityResponseDto
     */
    @Override
    public AmenityResponseDto findById(Long id) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE + id));
        return amenityMapper.toResponseDto(amenity);
    }

    /**
     * Update an Amenity by id
     * 
     * @param id
     * @param request
     * @return AmenityResponseDto
     */
    @Override
    public AmenityResponseDto update(Long id, AmenityRequestDto request) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE + id));

        amenity.setName(request.name());
        amenity.setDescription(request.description());
        Amenity saved = amenityRepository.save(amenity);

        return amenityMapper.toResponseDto(saved);
    }

    /**
     * Delete an Amenity by id
     * 
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        if (!amenityRepository.existsById(id)) {
            throw new EntityNotFoundException(NOT_FOUND_MESSAGE + id);
        }
        amenityRepository.deleteById(id);
    }
}