package com.hotelsbook.services.com_hotelsbook_services.mapper;

import org.springframework.stereotype.Component;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.CityRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.entity.City;

@Component
public class CityMapper {
    
    public City toEntity(CityRequestDto dto) 
    {
        if (dto == null) return null;

        return new City(
            dto.name(),
            dto.country()
        );
    }
}
