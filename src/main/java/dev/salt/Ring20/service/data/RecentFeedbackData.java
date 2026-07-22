package dev.salt.Ring20.service.data;

import dev.salt.Ring20.entity.Feedback;

import java.util.List;
import java.util.Map;

public record RecentFeedbackData(
        List<Feedback> feedbacks,
        Map<Long, String> workoutNameById
) {
}
