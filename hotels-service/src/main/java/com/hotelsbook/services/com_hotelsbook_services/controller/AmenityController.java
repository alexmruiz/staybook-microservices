package com.hotelsbook.services.com_hotelsbook_services.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.AmenityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.AmenityResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.service.AmenityService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/amenities")
public class AmenityController {

    private final AmenityService amenityService;

    public AmenityController(AmenityService amenityService) {
        this.amenityService = amenityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AmenityResponseDto create(@Valid @RequestBody AmenityRequestDto requestDto) {
        return amenityService.create(requestDto);
    }

    @GetMapping
    public List<AmenityResponseDto> findAll() {
        return amenityService.findAll();
    }

    @GetMapping("/{id}")
    public AmenityResponseDto findById(@PathVariable Long id)
    {
        return amenityService.findById(id);
    }

    @PutMapping("/{id}")
    public AmenityResponseDto update(@PathVariable Long id, @Valid @RequestBody AmenityRequestDto amenityRequestDto) {
        return amenityService.update(id, amenityRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        amenityService.deleteById(id);
    }

}
