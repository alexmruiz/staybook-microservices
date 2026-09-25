package com.hotelsbook.services.com_hotelsbook_services.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotelsbook.services.com_hotelsbook_services.entity.City;
import com.hotelsbook.services.com_hotelsbook_services.entity.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByAddressCityId(Long cityId);
    Long countByAddressCity(City city);
    Optional<Hotel> findByNameAndAddressCity(String name, City city);
    List<Hotel> findByStarsGreaterThanEqual(Integer stars);
}
