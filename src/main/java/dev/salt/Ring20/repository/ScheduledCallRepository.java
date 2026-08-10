package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.CallBackStatus;
import dev.salt.Ring20.entity.DayOfWeekType;
import dev.salt.Ring20.entity.ScheduledCall;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduledCallRepository extends JpaRepository<ScheduledCall, Long> {

    @Query(
            "SELECT c FROM ScheduledCall c WHERE c.targetTime BETWEEN :start AND :end AND c.callBackStatus = 'PENDING'")
    List<ScheduledCall> findCallsBetween(Instant start, Instant end);

    @Query(
            "SELECT c FROM ScheduledCall c WHERE c.targetTime < :time AND c.callBackStatus = 'PENDING'")
    List<ScheduledCall> findMissedCalls(@Param("time") Instant time);

    boolean existsByUserIdAndTargetTime(Long userId, Instant targetTime);
    void deleteByUserIdAndTargetTimeAfterAndCallBackStatus(
            Long userId,
            Instant time,
            CallBackStatus status
    );

    void deleteByUserIdAndDayAndTargetTimeAfterAndCallBackStatus(
            Long id, DayOfWeekType day, Instant now, CallBackStatus callBackStatus);
}
