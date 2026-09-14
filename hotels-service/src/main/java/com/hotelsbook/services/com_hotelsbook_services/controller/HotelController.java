package com.hotelsbook.services.com_hotelsbook_services.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.HotelRequestDto;
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
    
}
