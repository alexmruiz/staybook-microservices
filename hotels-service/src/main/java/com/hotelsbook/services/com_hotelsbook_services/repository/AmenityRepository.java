package com.hotelsbook.services.com_hotelsbook_services.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotelsbook.services.com_hotelsbook_services.entity.Amenity;

public interface AmenityRepository extends JpaRepository <Amenity, Long>{
    
}
