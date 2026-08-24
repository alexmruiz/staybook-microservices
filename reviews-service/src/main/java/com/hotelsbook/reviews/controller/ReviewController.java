package com.hotelsbook.reviews.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hotelsbook.reviews.dto.ReviewRequestDto;
import com.hotelsbook.reviews.dto.ReviewResponseDto;
import com.hotelsbook.reviews.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("api/hotels")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una nueva reseña", description = "Guarda una nueva reseña en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos")
    })
    public ReviewResponseDto create(@Valid @RequestBody ReviewRequestDto request) {
        return reviewService.create(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> findById(@PathVariable Long id) {
        ReviewResponseDto response = reviewService.findById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ReviewResponseDto>> findAll() {
        return ResponseEntity.ok(reviewService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> update(@PathVariable Long id,
            @Valid @RequestBody ReviewRequestDto request) {
        ReviewResponseDto responseDto = reviewService.update(id, request);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }

}
