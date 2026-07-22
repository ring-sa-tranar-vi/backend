package dev.salt.Ring20.service.data;

import dev.salt.Ring20.entity.ActivityLog;

import java.util.List;
import java.util.Map;

public record RecentActivityData(
        List<ActivityLog> activityLogs,
        Map<Long, String> userNameById,
        Map<Long, String> workoutNameById
) {
}
