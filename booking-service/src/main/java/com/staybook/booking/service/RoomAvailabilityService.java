package com.staybook.booking.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.staybook.booking.dto.response.RoomAvailabilityResponseDto;
import com.staybook.booking.entity.RoomAvailability;
import com.staybook.booking.exception.InvalidDateRangeException;
import com.staybook.booking.exception.RoomNotAvailableException;
import com.staybook.booking.mapper.RoomAvailabilityMapper;
import com.staybook.booking.repository.RoomAvailabilityRepository;

@Service
public class RoomAvailabilityService {

    private RoomAvailabilityRepository repository;
    private RoomAvailabilityMapper mapper;

    public RoomAvailabilityService(RoomAvailabilityRepository repository, RoomAvailabilityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Retorna una lista con todas las habitaciones disponibles en la fechas
     * indicadas
     * 
     * @param Long      hotelId
     * @param LocalDate startDate
     * @param LocalDate endDate
     * @param int       availableQuantity
     * @return List<RoomAvailabilityResponseDto> getAvailableRooms
     */
    public List<RoomAvailabilityResponseDto> getAvailableRooms(Long hotelId, LocalDate startDate, LocalDate endDate,
            int availableQuantity) {

        if (startDate == null || endDate == null || !startDate.isBefore(endDate)) {
            throw new InvalidDateRangeException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        List<RoomAvailability> roomAvailabilities = repository.getAvailableRooms(hotelId, startDate, endDate,
                availableQuantity);

        if (roomAvailabilities.isEmpty()) {
            throw new RoomNotAvailableException("No hay habitaciones disponibles en la fecha indicada");
        }
        
        return roomAvailabilities.stream().map(mapper::toResponseDto).toList();
    }
}
