package com.hotelsbook.services.com_hotelsbook_services.mapper;

import org.springframework.stereotype.Component;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.AddressRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.entity.Address;
import com.hotelsbook.services.com_hotelsbook_services.entity.City;

@Component
public class AddressMapper {
    
    private final CityMapper cityMapper;

    public AddressMapper(CityMapper cityMapper)
    {
        this.cityMapper = cityMapper;
    }

    public Address toEntity(AddressRequestDto dto)
    {
        if (dto == null) return null;

        City city = cityMapper.toEntity(dto.city());

        return new Address(
            dto.street(),
            dto.streetNumber(),
            dto.postalCode(),
            city
        );
    }
}
