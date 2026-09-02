package com.hotelsbook.services.com_hotelsbook_services.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.CityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.CityResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.service.CityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cities")
public class CityController {
    
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CityResponseDto create(@Valid @RequestBody CityRequestDto cityRequestDto) {
        return cityService.create(cityRequestDto);
    }

    @GetMapping
    public ResponseEntity<List<CityResponseDto>> findAll() {
        return ResponseEntity.ok(cityService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(cityService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CityResponseDto> update(
            @PathVariable Long id, 
            @Valid @RequestBody CityRequestDto request) {
        return ResponseEntity.ok(cityService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        cityService.deleteById(id);
    }
}
