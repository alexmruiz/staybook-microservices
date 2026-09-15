package com.hotelsbook.services.com_hotelsbook_services.mapper;

import org.springframework.stereotype.Component;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.AmenityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.AmenityResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.entity.Amenity;

@Component
public class AmenityMapper {
    
    public Amenity toEntity(AmenityRequestDto dto)
    {
        if (dto == null) return null;

        return new Amenity(dto.name(), dto.description());
    }

    public AmenityResponseDto toResponseDto(Amenity entity)
    {
        if (entity == null) return null;

        return new AmenityResponseDto(entity.getId(), entity.getName(), entity.getDescription());
    }
}
