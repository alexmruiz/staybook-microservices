package com.hotelsbook.services.com_hotelsbook_services.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.hotelsbook.services.com_hotelsbook_services.entity.Address;
import com.hotelsbook.services.com_hotelsbook_services.entity.City;
import com.hotelsbook.services.com_hotelsbook_services.entity.Hotel;

@DataJpaTest(properties = "spring.sql.init.mode=never")
class HotelRepositoryTest {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private CityRepository cityRepository;

    @Test
    void findByAddressCityId_ShouldReturnHotelsFromCity() {
        City city = new City("Madrid", "España");
        city = cityRepository.save(city);

        Address address = new Address("Gran Vía", "12", "28013", city);
        Hotel hotel = new Hotel("Hotel Gran Vía", "Descripción", address, 5, 100);

        hotelRepository.save(hotel);

        List<Hotel> result = hotelRepository.findByAddressCityId(city.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Hotel Gran Vía");
    }

    @Test
    void countByAddressCity_ShouldCountHotelsInCity() {
        City city = new City("Madrid", "España");
        city = cityRepository.save(city);

        Address address1 = new Address("Gran Vía", "12", "28013", city);
        Address address2 = new Address("Alcalá", "30", "28014", city);

        hotelRepository.save(new Hotel("Hotel 1", "Desc 1", address1, 4, 80));
        hotelRepository.save(new Hotel("Hotel 2", "Desc 2", address2, 5, 120));

        Long count = hotelRepository.countByAddressCity(city);

        assertThat(count).isEqualTo(2L);
    }

    @Test
    void findByNameAndAddressCity_ShouldReturnHotelWhenItExists() {
        City city = new City("Madrid", "España");
        city = cityRepository.save(city);

        Address address = new Address("Gran Vía", "12", "28013", city);
        Hotel hotel = new Hotel("Hotel Gran Vía", "Descripción", address, 5, 100);

        hotelRepository.save(hotel);

        Optional<Hotel> result = hotelRepository.findByNameAndAddressCity("Hotel Gran Vía", city);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Hotel Gran Vía");
    }

    @Test
    void findByStarsGreaterThanEqual_ShouldReturnHotelsWithMinimumStars() {
        City city = new City("Madrid", "España");
        city = cityRepository.save(city);

        Address address1 = new Address("Gran Vía", "12", "28013", city);
        Address address2 = new Address("Alcalá", "30", "28014", city);

        hotelRepository.save(new Hotel("Hotel 3", "Desc 3", address1, 3, 50));
        hotelRepository.save(new Hotel("Hotel 5", "Desc 5", address2, 5, 100));

        List<Hotel> result = hotelRepository.findByStarsGreaterThanEqual(4);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Hotel 5");
    }
}