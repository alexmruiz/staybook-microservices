package com.hotelsbook.hotel.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotelsbook.hotel.entity.City;
import com.hotelsbook.hotel.repository.CityRepository;

@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    /**
     * Busca una ciudad por nombre y devuelve el id
     * @param name
     * @return
     */
    public Integer getCityByName(String name) {
        Optional<City> city = cityRepository.findByName(name);
        Integer id = 0;
        if (city.isPresent())
            id = city.get().getId();

        return id;
    }

}
