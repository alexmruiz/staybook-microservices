package com.hotelsbook.reviews.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotelsbook.reviews.entity.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findByHotelId(Long hotelId);
}
