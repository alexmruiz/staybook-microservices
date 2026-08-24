package com.hotelsbook.reviews.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hotelsbook.reviews.dto.ReviewRequestDto;
import com.hotelsbook.reviews.dto.ReviewResponseDto;
import com.hotelsbook.reviews.entity.ReviewEntity;
import com.hotelsbook.reviews.mapper.ReviewMapper;
import com.hotelsbook.reviews.repository.ReviewRepository;

@Service
public class ReviewService {

    
    private final ReviewRepository reviewRepository;

    private final ReviewMapper reviewMapper;

    public ReviewService(ReviewRepository reviewRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    /**
     * Create
     * @param request ReviewRequestDto
     * @return ReviewResponseDto
     */
    public ReviewResponseDto create(ReviewRequestDto request) 
    {
        ReviewEntity reviewEntity = reviewMapper.toEntity(request);
        ReviewEntity saved = reviewRepository.save(reviewEntity);
        return reviewMapper.toResponseDto(saved);
    }

    /**
     * find all
     * @return List ReviewResponseDto
     */
    public List<ReviewResponseDto> findAll() 
    {
        return reviewRepository.findAll()
            .stream()
            .map(reviewMapper::toResponseDto)
            .toList();
    }
    
    /**
     * find by id
     * @param id
     * @return
     */
    public ReviewResponseDto findById(Long id)
    {
        ReviewEntity review = reviewRepository.findById(id).orElseThrow(()-> new RuntimeException("Reseña no encontrada con Id: " + id));

        return reviewMapper.toResponseDto(review);
    }

    /**
     * Update a review
     * @param Long id
     * @param ReviewRequestDto request
     * @return ReviewResponseDto
     */
    public ReviewResponseDto update(Long id, ReviewRequestDto request)
    {
        ReviewEntity existReview = reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Reseña no encontrada"));

        existReview.setAverageCalification(request.getAverageCalification());
        existReview.setHotelId(request.getHotelId());

        ReviewEntity update = reviewRepository.save(existReview);

        return reviewMapper.toResponseDto(update);
    }
    /**
     * Delete by id
     * @param id
     */
    public void delete(Long hotelId)
    {
        if (!reviewRepository.existsByHotelId(hotelId))
            throw new RuntimeException("No se puede eliminar. Reseña no encontrada");

        reviewRepository.deleteById(hotelId);
    }

}
