package com.hotelsbook.services.com_hotelsbook_services.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotelsbook.services.com_hotelsbook_services.entity.City;

public interface CityRepository extends JpaRepository<City, Long>{

    Optional<City> findByName(String name);
    
}
