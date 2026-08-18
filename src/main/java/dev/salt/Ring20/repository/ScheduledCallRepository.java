package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.ScheduledCall;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduledCallRepository extends JpaRepository<ScheduledCall, Long> {

    @Query(
            "SELECT c FROM ScheduledCall c WHERE c.targetTime BETWEEN :start AND :end AND c.callBackStatus = 'PENDING'")
    List<ScheduledCall> findCallsBetween(Instant start, Instant end);

    @Query(
            "SELECT c FROM ScheduledCall c WHERE c.targetTime < :time AND c.callBackStatus = 'PENDING'")
    List<ScheduledCall> findAllMissedCalls(@Param("time") Instant time);

    boolean existsByUserIdAndTargetTime(Long userId, Instant targetTime);

    @Modifying
    @Query(
            """
                UPDATE ScheduledCall c
                SET c.callbackPreference = NULL
                WHERE c.callbackPreference.id = :preferenceId
                  AND c.targetTime < CURRENT_TIMESTAMP
            """)
    void detachPreferenceFromHistoricalCalls(@Param("preferenceId") Long preferenceId);

    @Modifying
    @Query(
            """
                                DELETE FROM ScheduledCall c
                                WHERE c.userId = :userId
                                  AND c.targetTime > :time
                        """)
    int deleteFutureCallsForUser(@Param("userId") Long userId, @Param("time") Instant time);

    @Modifying
    @Query(
            """
                                DELETE FROM ScheduledCall c
                                WHERE c.callbackPreference.id = :preferenceId
                                  AND c.targetTime > :targetTime
                        """)
    int deleteFutureCallsForPreference(
            @Param("preferenceId") Long preferenceId, @Param("targetTime") Instant targetTime);

    @Query(
            """
                SELECT COUNT(c)
                FROM ScheduledCall c
                WHERE c.callbackPreference.id = :prefId
                  AND c.targetTime > :now
                  AND c.callBackStatus = 'PENDING'
            """)
    long countFuturePendingCalls(@Param("prefId") Long prefId, @Param("now") Instant now);

    List<ScheduledCall> findByUserId(Long userId);

    @Modifying
    @Query(
"""
    UPDATE ScheduledCall c
    SET c.fcmToken = :token
    WHERE c.userId = :userId
      AND c.targetTime > :now
      AND c.callBackStatus = 'PENDING'
""")
    int updateFcmTokenForFuturePendingCalls(
            @Param("userId") Long userId, @Param("token") String token, @Param("now") Instant now);
}
