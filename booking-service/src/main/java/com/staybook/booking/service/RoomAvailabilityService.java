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

    private final RoomAvailabilityRepository repository;
    private final RoomAvailabilityMapper mapper;

    public RoomAvailabilityService(RoomAvailabilityRepository repository, RoomAvailabilityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Devuelve una lista con los días disponibles, si la disponibilidad es nula lanza excepción.
     * Si la disponibilidad es parcial muestra una lista con los días disponibles
     * @param hotelId
     * @param roomTypeId
     * @param startDate
     * @param endDate
     * @param roomsRequested
     * @return List<RoomAvailabilityResponseDto> getAvailableRooms
     */
    public List<RoomAvailabilityResponseDto> getAvailableRooms(Long hotelId, Long roomTypeId, LocalDate startDate, LocalDate endDate,
            int roomsRequested) {

        if (startDate == null || endDate == null || !startDate.isBefore(endDate)) {
            throw new InvalidDateRangeException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        List<RoomAvailability> roomAvailabilities = repository.getAvailableRooms(hotelId, startDate, endDate,
                roomsRequested, roomTypeId);

        if (roomAvailabilities.isEmpty()) {
            throw new RoomNotAvailableException("No hay habitaciones disponibles en la fecha indicada");
        }
        
        return roomAvailabilities.stream().map(mapper::toResponseDto).toList();
    }
}
