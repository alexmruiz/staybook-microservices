package com.hotelsbook.services.com_hotelsbook_services.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotelsbook.services.com_hotelsbook_services.entity.HotelServiceEntity;

@Repository
public interface HotelServiceRepository extends JpaRepository<HotelServiceEntity, Long> {

    @Query(value = "call GetServicesByHotels(:hotelIds)", nativeQuery = true)
    List<Object[]> getServicesByHotels(@Param("hotelIds") String hotelIds);

}
