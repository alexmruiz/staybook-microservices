package com.hotelsbook.services.com_hotelsbook_services.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.CityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.CityResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.entity.City;
import com.hotelsbook.services.com_hotelsbook_services.exception.EntityNotFoundException;
import com.hotelsbook.services.com_hotelsbook_services.mapper.CityMapper;
import com.hotelsbook.services.com_hotelsbook_services.repository.CityRepository;

@Service
public class CityService implements CrudService<CityRequestDto, CityResponseDto> {

    private final CityRepository cityRepository;

    private final CityMapper cityMapper;

    private static final String CITY_NOT_FOUND = "Ciudad no encontrada con el id: ";

    public CityService(CityRepository cityRepository, CityMapper cityMapper) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
    }

    /**
     * Create a City
     * 
     * @param CityRequestDto cityRequestDto
     * @return CityResponseDto
     */
    @Override
    public CityResponseDto create(CityRequestDto cityRequestDto) {
        City city = cityMapper.toEntity(cityRequestDto);
        City citySaved = cityRepository.save(city);
        return cityMapper.toResponseDto(citySaved);
    }

    @Override
    public Page<CityResponseDto> findAll(Pageable pageable) {
        return cityRepository.findAll(pageable)
                .map(cityMapper::toResponseDto);
    }

    /**
     * Find a City by id
     * 
     * @param Long id
     * @return CityResponseDto
     */
    @Override
    public CityResponseDto findById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CITY_NOT_FOUND + id));
        return cityMapper.toResponseDto(city);
    }

    /**
     * Update a city by id
     * 
     * @param Long           id
     * @param CityRequestDto request
     * @return CityResponseDto
     */
    @Override
    public CityResponseDto update(Long id, CityRequestDto request) {
        // Comprobar si existe
        City existCity = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CITY_NOT_FOUND + id));

        // actualizar objeto
        existCity.setName(request.name());
        existCity.setCountry(request.country());
        City update = cityRepository.save(existCity);

        // devolver dto
        return cityMapper.toResponseDto(update);
    }

    /**
     * Delete a city by id
     * 
     * @param Long id
     */
    @Override
    public void deleteById(Long id) {
        City existCity = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CITY_NOT_FOUND + id));

        cityRepository.delete(existCity);
    }

}
