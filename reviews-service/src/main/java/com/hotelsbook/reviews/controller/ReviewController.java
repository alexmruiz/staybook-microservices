package com.hotelsbook.reviews.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hotelsbook.reviews.dto.request.ReviewRequestDto;
import com.hotelsbook.reviews.dto.response.ReviewResponseDto;
import com.hotelsbook.reviews.service.ReviewService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponseDto create(@Valid @RequestBody ReviewRequestDto request) {
        return reviewService.create(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> findAll() {
        return ResponseEntity.ok(reviewService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDto request) {
        return ResponseEntity.ok(reviewService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }

    @GetMapping("/hotels")
    public ResponseEntity<List<ReviewResponseDto>> findByHotelId(@RequestParam Long hotelId) {
        return ResponseEntity.ok(reviewService.findByHotelId(hotelId));
    }

}
