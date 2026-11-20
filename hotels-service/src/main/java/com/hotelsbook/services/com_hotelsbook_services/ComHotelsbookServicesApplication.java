package com.hotelsbook.services.com_hotelsbook_services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableCaching(proxyTargetClass = true)
@SpringBootApplication
@EnableFeignClients 
public class ComHotelsbookServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComHotelsbookServicesApplication.class, args);
	}

}
