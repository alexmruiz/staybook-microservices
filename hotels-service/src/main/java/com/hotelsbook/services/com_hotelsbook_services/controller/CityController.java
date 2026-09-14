package com.hotelsbook.services.com_hotelsbook_services.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.CityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.CityResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.HotelResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.service.CityService;
import com.hotelsbook.services.com_hotelsbook_services.service.HotelService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;
    private final HotelService hotelService;

    public CityController(CityService cityService, HotelService hotelService) {
        this.cityService = cityService;
        this.hotelService = hotelService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CityResponseDto create(@Valid @RequestBody CityRequestDto cityRequestDto) {
        return cityService.create(cityRequestDto);
    }

    @GetMapping
    public Page<CityResponseDto> findAll(Pageable pageable) {
        return cityService.findAll(pageable);
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

    @GetMapping("/{cityId}/hotels")
    public ResponseEntity<List<HotelResponseDto>> findHotelsByCity(@PathVariable Long cityId) {
        return ResponseEntity.ok(hotelService.findByCity(cityId));
    }
}
