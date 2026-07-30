package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.ScheduledCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface ScheduledCallRepository extends JpaRepository<ScheduledCall, Long> {

    @Query("SELECT c FROM ScheduledCall c WHERE c.targetTime BETWEEN :start AND :end AND c.callBackStatus = 'PENDING'")
    List<ScheduledCall> findCallsBetween(Instant start, Instant end);
}
