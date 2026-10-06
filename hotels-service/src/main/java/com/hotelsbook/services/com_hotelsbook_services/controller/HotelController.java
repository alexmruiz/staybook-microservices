package com.hotelsbook.services.com_hotelsbook_services.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.HotelRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.HotelDetailResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.HotelResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.service.HotelService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService service;

    public HotelController(HotelService service)
    {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HotelResponseDto create(@Valid @RequestBody HotelRequestDto request)
    {
        return service.create(request);
    }

    @GetMapping
    public Page<HotelResponseDto> findAll(Pageable pageable)
    {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public HotelResponseDto findById(@PathVariable Long id)
    {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public HotelResponseDto update(@PathVariable Long id, @Valid @RequestBody HotelRequestDto requestDto)
    {
        return service.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)
    {
        service.deleteById(id);
    }

    @GetMapping("/{id}/details")
    public HotelDetailResponseDto getHotelWithReviews(@PathVariable("id") Long id) {
        return service.getHotelWithReviews(id);
    }
}
