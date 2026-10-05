package com.staybook.booking.integration;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.staybook.booking.client.HotelsClient;
import com.staybook.booking.service.ExternalHotelService;

@SpringBootTest 
@ActiveProfiles("test")
class ExternalHotelServiceCircuitBreakerIntegrationTest {
    @Autowired 
    private ExternalHotelService service;

    @MockitoBean 
    private HotelsClient hotelsClient; // o @MockBean según versión de Spring Boot

    @Test 
    void circuitOpens_afterRepeatedFailures_andReturnsFallback() {
        when(hotelsClient.getHotelById(any())).thenThrow(new RuntimeException("down"));
        for (int i = 0; i < 5; i++) {
            service.getHotel(1L); // alimenta la ventana deslizante configurada (size=5)
        }

        // La 6ª llamada debería ir directa al fallback sin tocar hotelsClient.
        assertNull(service.getHotel(1L));
        verify(hotelsClient, times(5)).getHotelById(any());
    }
}
