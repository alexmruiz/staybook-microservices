package com.hotelsbook.services.com_hotelsbook_services.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.hotelsbook.services.com_hotelsbook_services.client.CountriesNowClient;
import com.hotelsbook.services.com_hotelsbook_services.dto.request.CityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.CityResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.entity.City;
import com.hotelsbook.services.com_hotelsbook_services.exception.EntityNotFoundException;
import com.hotelsbook.services.com_hotelsbook_services.mapper.CityMapper;
import com.hotelsbook.services.com_hotelsbook_services.repository.CityRepository;

import static org.mockito.Mockito.mock;

@SpringJUnitConfig(CityServiceCacheTest.TestConfiguration.class)
class CityServiceCacheTest {

    private static final Long CITY_ID = 1L;

    @org.springframework.beans.factory.annotation.Autowired
    private CityService cityService;

    @org.springframework.beans.factory.annotation.Autowired
    private CacheManager cacheManager;

    @org.springframework.beans.factory.annotation.Autowired
    private CityRepository cityRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private CityMapper cityMapper;

    private City city;
    private CityResponseDto cityResponseDto;

    @BeforeEach
    void setUp() {
        cacheManager.getCache("cities").clear();
        reset(cityRepository, cityMapper);

        city = new City("Madrid", "España");
        city.setId(CITY_ID);
        cityResponseDto = new CityResponseDto(CITY_ID, "Madrid", "España");
    }

    @Test
    void findById_WhenCalledTwice_ShouldUseCacheOnSecondCall() {
        when(cityRepository.findById(CITY_ID)).thenReturn(Optional.of(city));
        when(cityMapper.toResponseDto(city)).thenReturn(cityResponseDto);

        CityResponseDto firstResult = cityService.findById(CITY_ID);
        CityResponseDto secondResult = cityService.findById(CITY_ID);

        assertEquals(cityResponseDto, firstResult);
        assertEquals(cityResponseDto, secondResult);
        verify(cityRepository, times(1)).findById(CITY_ID);
        verify(cityMapper, times(1)).toResponseDto(city);
    }

    @Test
    void update_WhenCityWasCached_ShouldEvictCachedValue() {
        when(cityRepository.findById(CITY_ID)).thenReturn(Optional.of(city));
        when(cityMapper.toResponseDto(city)).thenReturn(cityResponseDto);
        cityService.findById(CITY_ID);

        reset(cityRepository, cityMapper);
        City updatedCity = new City("Barcelona", "España");
        updatedCity.setId(CITY_ID);
        CityResponseDto updatedResponse = new CityResponseDto(CITY_ID, "Barcelona", "España");
        when(cityRepository.findById(CITY_ID)).thenReturn(Optional.of(city));
        when(cityRepository.save(city)).thenReturn(updatedCity);
        when(cityMapper.toResponseDto(updatedCity)).thenReturn(updatedResponse);

        cityService.update(CITY_ID, new CityRequestDto("Barcelona", "España"));

        when(cityRepository.findById(CITY_ID)).thenReturn(Optional.of(updatedCity));
        CityResponseDto resultAfterUpdate = cityService.findById(CITY_ID);

        assertEquals(updatedResponse, resultAfterUpdate);
        verify(cityRepository, times(2)).findById(CITY_ID);
        verify(cityMapper, times(2)).toResponseDto(updatedCity);
    }

    @Test
    void delete_WhenCityWasCached_ShouldEvictCachedValue() {
        when(cityRepository.findById(CITY_ID)).thenReturn(Optional.of(city));
        when(cityMapper.toResponseDto(city)).thenReturn(cityResponseDto);
        cityService.findById(CITY_ID);

        reset(cityRepository, cityMapper);
        when(cityRepository.findById(CITY_ID)).thenReturn(Optional.of(city));
        cityService.deleteById(CITY_ID);

        when(cityRepository.findById(CITY_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cityService.findById(CITY_ID));
        verify(cityRepository, times(2)).findById(CITY_ID);
    }

    @Configuration
    @EnableCaching(proxyTargetClass = true)
    static class TestConfiguration {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("cities");
        }

        @Bean
        CityRepository cityRepository() {
            return mock(CityRepository.class);
        }

        @Bean
        CityMapper cityMapper() {
            return mock(CityMapper.class);
        }

        @Bean
        CountriesNowClient countriesNowClient() {
            return mock(CountriesNowClient.class);
        }

        @Bean
        CityService cityService(
                CityRepository cityRepository,
                CityMapper cityMapper,
                CountriesNowClient countriesNowClient) {
            return new CityService(cityRepository, cityMapper, countriesNowClient);
        }
    }
}
