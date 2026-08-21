package com.hotelsbook.hotel.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotelsbook.hotel.dto.HotelAvailableDto;
import com.hotelsbook.hotel.response.ErrorResponse;
import com.hotelsbook.hotel.service.CityService;
import com.hotelsbook.hotel.service.HotelService;

@CrossOrigin(origins = "http://localhost:4200/")
@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private static final Logger logger = LoggerFactory.getLogger(HotelController.class);

    @Autowired
    private HotelService hotelService;

    @Autowired
    private CityService cityService;

    /**
     * Obtiene los hoteles disponibles con sus servicios y reseñas en una ciudad
     * específica dentro de un rango de fechas.
     * 
     * @param startDate
     * @param endDate
     * @param cityId
     * @return
     */
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableHotelsWithServices(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam("cityName") String cityName) {

        try {
            logger.info("método getAvailableHotelsWtichServices");

            Integer cityId = cityService.getCityByName(cityName);

            if (cityId == 0) {
                return new ResponseEntity<>(new ErrorResponse(404, "No se encontraron registros"),
                        HttpStatus.NOT_FOUND);
            }

            List<HotelAvailableDto> hotels = hotelService.getAvailableHotelsWithServicesAndReviews(startDate, endDate,
                    cityId);

            if (hotels.isEmpty()) {
                return new ResponseEntity<>(new ErrorResponse(404, "No se encontraron registros"),
                        HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok(hotels);

        } catch (Exception e) {
            logger.error("error en getAvailableHotelsWithServices", e);
            ErrorResponse error = new ErrorResponse(500, "Error interno del servidor");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
