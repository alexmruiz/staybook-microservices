package com.hotelsbook.services.com_hotelsbook_services.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hotelsbook.services.com_hotelsbook_services.client.CountriesNowClient;
import com.hotelsbook.services.com_hotelsbook_services.dto.request.CityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.request.CountriesNowRequest;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.CityImportResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.CityResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.CountriesNowResponse;
import com.hotelsbook.services.com_hotelsbook_services.entity.City;
import com.hotelsbook.services.com_hotelsbook_services.exception.EntityNotFoundException;
import com.hotelsbook.services.com_hotelsbook_services.mapper.CityMapper;
import com.hotelsbook.services.com_hotelsbook_services.repository.CityRepository;

@Service
public class CityService implements CrudService<CityRequestDto, CityResponseDto> {

    private final CityRepository cityRepository;

    private final CityMapper cityMapper;

    private final CountriesNowClient countriesNowClient;

    private static final String CITY_NOT_FOUND = "Ciudad no encontrada con el id: ";

    public CityService(CityRepository cityRepository, CityMapper cityMapper, CountriesNowClient countriesNowClient) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
        this.countriesNowClient = countriesNowClient;
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

    public CityImportResponseDto importCitiesForcountry(String countryName) {
        CountriesNowRequest request = new CountriesNowRequest(countryName);
        CountriesNowResponse response = countriesNowClient.getCitiesByCountry(request);

        if (response == null || response.error() || response.data() == null) {
            throw new RuntimeException("Error al obtener ciudades de la API externa para el país: " + countryName);
        }

        int insertedCount = 0;
        int discardedCount = 0;

        for (String cityName : response.data()) {
            if (cityName == null || cityName.isBlank() || cityName.length() > 50) {
                discardedCount++;
                continue;
            }

            boolean exists = cityRepository.findByName(cityName).isPresent();
            if (!exists) {
                City city = new City();
                city.setName(cityName.trim());
                city.setCountry(countryName.trim());
                cityRepository.save(city);
                insertedCount++;
            } else {
                discardedCount++;
            }
        }

        return new CityImportResponseDto(countryName, insertedCount, discardedCount);

    }



}
