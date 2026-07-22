package dev.salt.Ring20.service.data;

import dev.salt.Ring20.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record UserSummaryData(
        List<User> users,
        Map<Long, LocalDateTime> lastCompletedAtByUserId
) {
}
