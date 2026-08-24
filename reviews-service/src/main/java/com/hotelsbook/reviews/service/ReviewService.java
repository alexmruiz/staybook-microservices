package com.hotelsbook.reviews.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hotelsbook.reviews.dto.ReviewRequestDto;
import com.hotelsbook.reviews.dto.ReviewResponseDto;
import com.hotelsbook.reviews.entity.ReviewEntity;
import com.hotelsbook.reviews.exception.ReviewNotFoundException;
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
     * 
     * @param request ReviewRequestDto
     * @return ReviewResponseDto
     */
    public ReviewResponseDto create(ReviewRequestDto request) {
        ReviewEntity reviewEntity = reviewMapper.toEntity(request);
        ReviewEntity saved = reviewRepository.save(reviewEntity);
        return reviewMapper.toResponseDto(saved);
    }

    /**
     * find all
     * 
     * @return List ReviewResponseDto
     */
    public List<ReviewResponseDto> findAll() {
        return reviewRepository.findAll()
                .stream()
                .map(reviewMapper::toResponseDto)
                .toList();
    }

    /**
     * find by id
     * 
     * @param id
     * @return
     */
    public ReviewResponseDto findById(Long id) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Reseña no encontrada con Id: " + id));

        return reviewMapper.toResponseDto(review);
    }

    /**
     * Update a review
     * 
     * @param Long             id
     * @param ReviewRequestDto request
     * @return ReviewResponseDto
     */
    public ReviewResponseDto update(Long id, ReviewRequestDto request) {
        ReviewEntity existReview = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Reseña no encontrada"));

        existReview.setQualification(request.getQualification());
        existReview.setHotelId(request.getHotelId());

        ReviewEntity update = reviewRepository.save(existReview);

        return reviewMapper.toResponseDto(update);
    }

    /**
     * Delete by id
     * 
     * @param id
     */
    public void delete(Long id) {
        boolean existsReview = reviewRepository.existsById(id);

        if (!existsReview) {
            throw new ReviewNotFoundException(
                    "No se encontró la reseña con id: " + id);
        }

        reviewRepository.deleteById(id);
    }

}
