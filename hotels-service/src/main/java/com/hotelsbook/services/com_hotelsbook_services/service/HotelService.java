package com.hotelsbook.services.com_hotelsbook_services.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hotelsbook.services.com_hotelsbook_services.dto.HotelServiceDTO;
import com.hotelsbook.services.com_hotelsbook_services.model.ServiceResponse;
import com.hotelsbook.services.com_hotelsbook_services.repository.HotelServiceRepository;

@Service
public class HotelService {

    @Autowired
    private HotelServiceRepository repository;


    public List<HotelServiceDTO> getServicesByHotels(String hotelIds) {

        List<Object[]> results = repository.getServicesByHotels(hotelIds);

        Map<Long, HotelServiceDTO> hotelServicesMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            Long hotelId = ((Number) row[0]).longValue();
            String hotelName = (String) row[1];
            String serviceName = (String) row[2];
            Long serviceId = ((Number) row[3]).longValue();

            HotelServiceDTO hotelServiceDTO = hotelServicesMap.getOrDefault(hotelId, 
            new HotelServiceDTO(hotelId, hotelName, new ArrayList<>()));

            hotelServiceDTO.getService().add(new ServiceResponse(serviceId, serviceName));
            hotelServicesMap.put(hotelId, hotelServiceDTO);

        }

        return new ArrayList<>(hotelServicesMap.values());
    }

}
