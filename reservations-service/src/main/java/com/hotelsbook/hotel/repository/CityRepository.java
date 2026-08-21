package com.hotelsbook.hotel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotelsbook.hotel.entity.City;

public interface CityRepository extends JpaRepository<City, Integer> {

    Optional<City> findByName(String name);

}
