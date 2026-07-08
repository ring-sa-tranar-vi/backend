package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.Feedback;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("FeedbackRepository Tests")
class FeedbackRepositoryTest {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Test
    void saveAndQueryByUserAndWorkoutWork() {
        Feedback feedback = new Feedback();
        feedback.setUserId(1L);
        feedback.setWorkoutId(2L);
        feedback.setRating(5);

        feedbackRepository.save(feedback);

        assertEquals(1, feedbackRepository.findByUserId(1L).size());
        assertEquals(1, feedbackRepository.findByWorkoutId(2L).size());
        assertEquals(1, feedbackRepository.findByUserIdAndWorkoutId(1L, 2L).size());
    }
}
