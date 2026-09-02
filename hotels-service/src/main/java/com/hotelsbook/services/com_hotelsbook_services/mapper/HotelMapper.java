package com.hotelsbook.services.com_hotelsbook_services.mapper;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.hotelsbook.services.com_hotelsbook_services.dto.request.HotelRequestDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.AmenityResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.HotelResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.dto.response.RoomTypeResponseDto;
import com.hotelsbook.services.com_hotelsbook_services.entity.Address;
import com.hotelsbook.services.com_hotelsbook_services.entity.Amenity;
import com.hotelsbook.services.com_hotelsbook_services.entity.Hotel;
import com.hotelsbook.services.com_hotelsbook_services.entity.RoomType;

@Component
public class HotelMapper {

    private final AddressMapper addressMapper;

    private final RoomTypeMapper roomTypeMapper;

    private final AmenityMapper amenityMapper;

    public HotelMapper(
            AddressMapper addressMapper,
            RoomTypeMapper roomTypeMapper,
            AmenityMapper amenityMapper) {
        this.addressMapper = addressMapper;
        this.roomTypeMapper = roomTypeMapper;
        this.amenityMapper = amenityMapper;
    }

    public Hotel toEntity(HotelRequestDto dto) {
        if (dto == null)
            return null;

        Address address = addressMapper.toEntity(dto.address());

        Hotel hotel = new Hotel(dto.name(), dto.description(), address, dto.stars(), dto.capacity());

        if (dto.roomTypes() != null) {
            dto.roomTypes().forEach(roomTypeDto -> {
                RoomType roomType = roomTypeMapper.toEntity(roomTypeDto);
                hotel.addRoomType(roomType);
            });
        }

        return hotel;

    }

    public HotelResponseDto toResponseDto(Hotel entity) {
        if (entity == null)
            return null;

        Set<RoomTypeResponseDto> listRoomTypesDto = new HashSet<>();

        for (RoomType roomType : entity.getRoomTypes()) {
            // Traducimos la habitación a DTO usando su propio mapper
            RoomTypeResponseDto dto = roomTypeMapper.toResponseDto(roomType);
            listRoomTypesDto.add(dto);
        }

        Set<AmenityResponseDto> listAmenityResponseDto = new HashSet<>();

        for (Amenity amenity : entity.getAmenities()) {
            AmenityResponseDto amenityResponseDto = amenityMapper.toResponseDto(amenity);
            listAmenityResponseDto.add(amenityResponseDto);
        }

        return new HotelResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStars(),
                entity.getCapacity(),
                listRoomTypesDto,
                listAmenityResponseDto);

    }

}
