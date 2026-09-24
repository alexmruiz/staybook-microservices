package com.hotelsbook.reviews.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.hotelsbook.reviews.entity.ReviewEntity;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository repository;

    @Test
    void findByHotelId_whenExists_returnsList() {
        // given
        ReviewEntity r1 = new ReviewEntity(10L, 1L, 4.5);
        ReviewEntity r2 = new ReviewEntity(10L, 1L, 3.0);
        repository.save(r1);
        repository.save(r2);

        // when
        List<ReviewEntity> results = repository.findByHotelId(10L);

        // then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(ReviewEntity::getHotelId).containsOnly(10L);
    }

    @Test
    void findByHotelId_whenNotExists_returnsEmpty() {
        // given
        ReviewEntity r = new ReviewEntity(20L, 1L, 5.0);
        repository.save(r);

        // when
        List<ReviewEntity> results = repository.findByHotelId(99L);

        // then
        assertThat(results).isEmpty();
    }
}
