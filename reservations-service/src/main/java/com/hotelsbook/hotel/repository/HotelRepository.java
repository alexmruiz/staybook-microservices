package com.hotelsbook.hotel.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotelsbook.hotel.entity.HotelAvailable;

public interface HotelRepository extends JpaRepository <HotelAvailable, Long>{

    @Query(value = "CALL GetAvailableHotelsByCity(:startDate, :endDate, :cityId)", nativeQuery = true)
    List<HotelAvailable> findAvailableHotelsByCity(
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate,
        @Param("cityId") Integer cityId
    );

    

}
