package com.hotelsbook.reviews;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase (replace = AutoConfigureTestDatabase.Replace.ANY)
class ReviewsApplicationTests {

	@Test
	void contextLoads() {
	}

}
