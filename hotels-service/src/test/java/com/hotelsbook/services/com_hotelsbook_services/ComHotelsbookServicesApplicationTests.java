package com.hotelsbook.services.com_hotelsbook_services;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase (replace = AutoConfigureTestDatabase.Replace.ANY)
class ComHotelsbookServicesApplicationTests {
    @Test
    void contextLoads() { }
}