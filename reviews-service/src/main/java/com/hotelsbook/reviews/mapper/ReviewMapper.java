package com.hotelsbook.reviews.mapper;

import org.springframework.stereotype.Component;

import com.hotelsbook.reviews.dto.ReviewRequestDto;
import com.hotelsbook.reviews.dto.ReviewResponseDto;
import com.hotelsbook.reviews.entity.ReviewEntity;

@Component
public class ReviewMapper {

    public ReviewEntity toEntity(ReviewRequestDto requestDto) {
        return new ReviewEntity(
                requestDto.getHotelId(),
                requestDto.getQualification()
        );
    }

    public ReviewResponseDto toResponseDto(ReviewEntity entity) {
        return new ReviewResponseDto(
                entity.getId(),
                entity.getHotelId(),
                entity.getQualification(),
                entity.getCreatedAt()
        );
    }
}
