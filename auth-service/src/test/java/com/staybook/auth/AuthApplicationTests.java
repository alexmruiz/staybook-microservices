package com.staybook.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(com.staybook.auth.config.TestBeansConfig.class)
class AuthApplicationTests {

	@Test
	void contextLoads() {
	}

}
