package com.hotelsbook.reviews.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotelsbook.reviews.entity.ReviewEntity;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    @Query(value = "CALL GetAverageCalificationByHotel(:hotelIds)", nativeQuery = true)
    List<Object[]> findAverageCalificationByHotel(@Param("hotelIds") String hotelIds);

}
