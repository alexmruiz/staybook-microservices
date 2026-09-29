package com.staybook.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(com.staybook.auth.config.TestBeansConfig.class)
@ActiveProfiles("test")
class AuthApplicationTests {

	@Test
	void contextLoads() {
	}

}
