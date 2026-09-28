package com.staybook.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.staybook.auth.entity.Auth;
import com.staybook.auth.enums.TypeRole;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase (replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource (properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
public class AuthRepositoryTest {
    
    @Autowired 
    private AuthRepository repository;

    @Test 
    void findByEmail_ShouldReturnAuthEntity() {

        // arrange
        Auth auth = new Auth();
        auth.setEmail("alice@example.com");
        auth.setPassword("s3cr3t");
        auth.setName("Alice");
        auth.setSurname("Example");
        auth.setRole(TypeRole.ROLE_USER);

        // act
        Auth saved = repository.save(auth);
        Optional<Auth> found = repository.findByEmail("alice@example.com");

        // assert
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getEmail()).isEqualTo("alice@example.com");
    }
}
