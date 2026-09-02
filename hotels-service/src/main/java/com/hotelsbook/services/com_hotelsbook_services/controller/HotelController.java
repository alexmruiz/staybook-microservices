package com.hotelsbook.services.com_hotelsbook_services.controller;

import java.util.List;

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
    public List<HotelResponseDto> findAll()
    {
        return service.findAll();
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
