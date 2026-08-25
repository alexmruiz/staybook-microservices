package com.hotelsbook.reviews.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotelsbook.reviews.entity.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

}
