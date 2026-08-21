package com.hotelsbook.services.com_hotelsbook_services.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotelsbook.services.com_hotelsbook_services.dto.HotelServiceDTO;
import com.hotelsbook.services.com_hotelsbook_services.response.ErrorResponse;
import com.hotelsbook.services.com_hotelsbook_services.service.HotelService;

@RestController
@RequestMapping("/api/hotels")
public class HotelServiceController {

    @Autowired
    private HotelService hotelService;

    @GetMapping("/services/{hotelIds}")
    public ResponseEntity<?> getHotelServices(@PathVariable("hotelIds") String hotelIds) {
        try{
            List<HotelServiceDTO> response = hotelService.getServicesByHotels(hotelIds);

            if (response.isEmpty()){
                return new ResponseEntity<>(new ErrorResponse(404, "No se encontraron los registros"), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e){
            ErrorResponse error = new ErrorResponse(500, "Error interno del servidor");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
