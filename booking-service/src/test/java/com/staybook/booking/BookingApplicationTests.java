package com.staybook.booking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles ("test")
@DataJpaTest 
class BookingApplicationTests {

	@Test
	void contextLoads() {
	}

}
