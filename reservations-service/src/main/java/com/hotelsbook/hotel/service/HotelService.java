package com.hotelsbook.hotel.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hotelsbook.hotel.client.HotelReviewClient;
import com.hotelsbook.hotel.client.HotelServiceClient;
import com.hotelsbook.hotel.dto.HotelAvailableDto;
import com.hotelsbook.hotel.dto.HotelReviewDto;
import com.hotelsbook.hotel.dto.HotelServiceDto;
import com.hotelsbook.hotel.dto.ServiceDto;
import com.hotelsbook.hotel.entity.HotelAvailable;
import com.hotelsbook.hotel.repository.HotelRepository;

@Service
public class HotelService {

	@Autowired
	private HotelRepository hotelRepository;

	@Autowired
	private HotelServiceClient hotelServiceClient;

	@Autowired
	private HotelReviewClient hotelReviewClient;

	@Value("${server.url}")
	private String serverUrl;

	public List<HotelAvailableDto> getAvailableHotelsWithServicesAndReviews(Date startDate, Date endDate,
			Integer cityId) {
		// Paso 1: Obtener hoteles disponibles
		List<HotelAvailable> availableHotels = hotelRepository.findAvailableHotelsByCity(startDate, endDate, cityId);

		if (availableHotels.isEmpty()) {
			List<HotelAvailableDto> dto = new ArrayList<>();
			return new ArrayList<>(dto);
		} else {
			// Paso 2: Obtener los IDs de los hoteles
			List<Long> hotelIds = availableHotels.stream().map(HotelAvailable::getId).collect(Collectors.toList());

			// Paso 3: Consumir el microservicio de servicios de hotel
			List<HotelServiceDto> hotelServices = hotelServiceClient.getHotelServices(hotelIds);

			// Paso 4: Consumir el microservicio de reviews de hotel
			List<HotelReviewDto> hotelReviews = hotelReviewClient.getHotelReviews(hotelIds);
			hotelServices.forEach(s -> {
				System.out.println("HotelServiceDto -> id=" + s.getHotelId() + ", services=" + s.getServices());
			});

			// Paso 5: Combinar los resultados de servicios y reviews
			Map<Long, List<ServiceDto>> servicesByHotelId = hotelServices.stream()
					.collect(Collectors.toMap(HotelServiceDto::getHotelId, HotelServiceDto::getServices));

			Map<Long, Double> reviewsByHotelId = hotelReviews.stream()
					.collect(Collectors.toMap(HotelReviewDto::getHotelId, HotelReviewDto::getAverageCalification));

			// Paso 6: Agrupar y mapear los resultados
			return availableHotels.stream().map(hotel -> {
				String imageUrl = serverUrl + "/images/" + hotel.getPicture();
				hotel.setPicture(imageUrl);
				HotelAvailableDto dto = new HotelAvailableDto(hotel);
				dto.setServices(servicesByHotelId.getOrDefault(hotel.getId(), Collections.emptyList()));
				dto.setAverageCalification(reviewsByHotelId.getOrDefault(hotel.getId(), null));
				return dto;
			}).collect(Collectors.toList());
		}
	}

}
