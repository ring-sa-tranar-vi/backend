package dev.salt.Ring20.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import dev.salt.Ring20.entity.CallbackPreference;
import dev.salt.Ring20.entity.ScheduledCall;
import dev.salt.Ring20.entity.enums.CallBackStatus;
import dev.salt.Ring20.entity.enums.RepeatType;
import dev.salt.Ring20.repository.CallbackPreferenceRepository;
import dev.salt.Ring20.repository.ScheduledCallRepository;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduledCallService {
    private final CallbackPreferenceRepository callbackPreferenceRepository;
    private final ScheduledCallRepository scheduledCallRepository;
    private static final int WEEKLY_PENDING_CALLS = 4;
    private static final Logger log = LoggerFactory.getLogger(ScheduledCallService.class);

    public ScheduledCallService(
            CallbackPreferenceRepository callbackPreferenceRepository,
            ScheduledCallRepository scheduledCallRepository) {
        this.callbackPreferenceRepository = callbackPreferenceRepository;
        this.scheduledCallRepository = scheduledCallRepository;
    }

    @Transactional
    public void ensureRollingCalls(CallbackPreference pref) {

        log.info(
                "Ensuring rolling calls for preference={}, user={}, repeat={}",
                pref.getId(),
                pref.getUser().getId(),
                pref.getRepeat());

        if (pref.getRepeat() == RepeatType.NEVER) {
            long existing =
                    scheduledCallRepository.countFuturePendingCalls(pref.getId(), Instant.now());

            log.info(
                    "Preference={} has {} future pending calls",
                    pref.getId(),
                    existing);

            if (existing == 0) {
                Instant next = calculateNext(pref);

                scheduledCallRepository.save(buildCall(pref, next));
                log.info(
                        "No pending call exists for non-repeating preference={}, creating call at {}",
                        pref.getId(),
                        next);
            } else {
                log.info(
                        "Non-repeating preference={} already has a pending call, nothing to do",
                        pref.getId());
            }

            return;
        }

        long existing =
                scheduledCallRepository.countFuturePendingCalls(pref.getId(), Instant.now());

        int toCreate = WEEKLY_PENDING_CALLS - (int) existing;

        if (toCreate <= 0) {
            log.info(
                    "Preference={} already has enough pending calls ({}), nothing to create",
                    pref.getId(),
                    existing);
            return;
        }

        log.info(
                "Preference={} needs {} additional calls",
                pref.getId(),
                toCreate);

        Instant nextTime = calculateNext(pref);

        while (toCreate > 0) {
            log.info(
                    "Checking whether call already exists for user={} at {}",
                    pref.getUser().getId(),
                    nextTime);

            if (!alreadyExists(pref.getUser().getId(), nextTime)) {
                ScheduledCall call = buildCall(pref, nextTime);
                scheduledCallRepository.save(call);

                log.info(
                        "Created scheduled call id={} for user={} at {}",
                        call.getId(),
                        pref.getUser().getId(),
                        nextTime);
                toCreate--;
            } else {
                log.info(
                        "Call already exists for user={} at {}, skipping",
                        pref.getUser().getId(),
                        nextTime);
            }

            nextTime = nextTime.plus(7, ChronoUnit.DAYS);
        }
    }

    private ScheduledCall buildCall(CallbackPreference pref, Instant time) {
        ScheduledCall call = new ScheduledCall();
        call.setUserId(pref.getUser().getId());
        call.setTrainerId(pref.getUser().getTrainerId());
        call.setTargetTime(time);
        call.setFcmToken(pref.getUser().getFcmToken());
        call.setCallBackStatus(CallBackStatus.PENDING);
        call.setCallbackPreference(pref);
        return call;
    }

    private boolean alreadyExists(Long userId, Instant time) {
        boolean exists =
                scheduledCallRepository.existsByUserIdAndTargetTime(userId, time);

        log.info(
                "Existing call check: user={}, time={}, exists={}",
                userId,
                time,
                exists);

        return exists;
    }

    private Instant calculateNext(CallbackPreference pref) {
        LocalDate today = LocalDate.now();
        LocalTime time = pref.getTime();

        DayOfWeek targetDay = DayOfWeek.valueOf(pref.getDay().name());

        LocalDate nextDate = today.with(TemporalAdjusters.nextOrSame(targetDay));

        if (nextDate.equals(today) && time.isBefore(LocalTime.now())) {
            nextDate = nextDate.plusWeeks(1);
        }

        Instant result =
                nextDate
                        .atTime(time)
                        .atZone(ZoneId.systemDefault())
                        .toInstant();

        log.info(
                "Calculated next call: preference={}, day={}, time={}, result={}",
                pref.getId(),
                targetDay,
                time,
                result);

        return result;
    }

    @Scheduled(fixedRate = 60000)
    public void handleScheduledCalls() {
        Instant now = Instant.now();
        log.info("Scheduled call job started. now={}", now);

        List<ScheduledCall> startingNow =
                scheduledCallRepository.findCallsBetween(now.minusSeconds(30), now.plusSeconds(60));

        log.info(
                "Found {} calls in trigger window",
                startingNow.size());

        for (ScheduledCall call : startingNow) {
            log.info(
                    "Processing call id={}, targetTime={}, status={}",
                    call.getId(),
                    call.getTargetTime(),
                    call.getCallBackStatus());

            if (call.getCallBackStatus() != CallBackStatus.PENDING) {

                log.info(
                        "Skipping call id={} because status is {}",
                        call.getId(),
                        call.getCallBackStatus());
                continue;
            }

            if (call.getFcmToken() == null || call.getFcmToken().isBlank()) {
                log.warn(
                        "Skipping call id={} because FCM token is missing",
                        call.getId());
                continue;
            }

            boolean sent = sendNotification(call);

            if (sent) {
                log.info(
                        "Notification sent successfully for call id={}",
                        call.getId());
                call.setCallBackStatus(CallBackStatus.TRIGGERED);
                scheduledCallRepository.save(call);
            } else {
                log.warn(
                        "Notification failed for call id={}",
                        call.getId());
            }
        }

        List<ScheduledCall> missedCalls = scheduledCallRepository.findAllMissedCalls(now.minusSeconds(60));

        log.info(
                "Found {} missed calls",
                missedCalls.size());

        for (ScheduledCall call : missedCalls) {

            if (call.getCallBackStatus() == CallBackStatus.PENDING) {
                log.warn(
                        "Marking call id={} as MISSED. targetTime={}",
                        call.getId(),
                        call.getTargetTime());

                call.setCallBackStatus(CallBackStatus.MISSED);
                scheduledCallRepository.save(call);
            }
        }
        List<CallbackPreference> preferences = callbackPreferenceRepository.findAll();
        log.info(
                "Checking {} callback preferences for rolling calls",
                preferences.size());

        for (CallbackPreference pref : preferences) {
            if (pref.getRepeat() == RepeatType.WEEKLY) {
                ensureRollingCalls(pref);
            }
        }
        log.info("Scheduled call job finished");
    }

    public boolean sendNotification(ScheduledCall call) {
        log.info(
                "Sending notification for call id={}, user={}, trainer={}",
                call.getId(),
                call.getUserId(),
                call.getTrainerId());
        try {
            Message message =
                    Message.builder()
                            .setToken(call.getFcmToken())
                            .putData("callId", String.valueOf(call.getId()))
                            .putData("trainerId", String.valueOf(call.getTrainerId()))
                            .putData("userId", String.valueOf(call.getUserId()))
                            .putData("type", "TRAINING_CALL")
                            .build();

            String response =
                    FirebaseMessaging.getInstance().send(message);

            log.info(
                    "FCM notification sent for call id={}, response={}",
                    call.getId(),
                    response);

            return true;

        } catch (Exception e) {
            log.error(
                    "Failed to send FCM notification for call id={}",
                    call.getId(),
                    e);

            return false;
        }
    }

    @Transactional
    public void completeCall(Long id) {
        log.info("Completing call id={}", id);

        ScheduledCall call =
                scheduledCallRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "No scheduled call exists with this id: " + id));
        if (call.getCallBackStatus() != CallBackStatus.TRIGGERED) {
            log.warn(
                    "Cannot complete call id={}: current status={}",
                    id,
                    call.getCallBackStatus());

            throw new IllegalStateException("Only triggered calls can be completed");
        }
        call.setCallBackStatus(CallBackStatus.COMPLETED);
        scheduledCallRepository.save(call);
        log.info("Call id={} marked as COMPLETED", id);
    }

    @Transactional
    public void cancelCall(Long callId) {
        log.info("Cancelling call id={}", callId);
        ScheduledCall call =
                scheduledCallRepository
                        .findById(callId)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Call not found with id: " + callId));

        if (call.getCallBackStatus() != CallBackStatus.PENDING) {
            log.warn(
                    "Cannot cancel call id={}: current status={}",
                    callId,
                    call.getCallBackStatus());

            throw new IllegalStateException(
                    "Only pending calls can be cancelled");
        }

        call.setCallBackStatus(CallBackStatus.CANCELLED);
        scheduledCallRepository.save(call);
        log.info("Call id={} marked as CANCELLED", callId);

    }

    @Transactional
    public void cancelFutureCallsForOnePreference(CallbackPreference pref) {
        log.info(
                "Cancelling future pending calls for preference={}",
                pref.getId());
        scheduledCallRepository.cancelFuturePendingCallsForPreference(pref.getId(), Instant.now());
    }

    @Transactional
    public void detachHistoricalCallsFromPreference(Long preferenceId) {
        log.info(
                "Detaching historical calls from preference={}",
                preferenceId);
        scheduledCallRepository.detachPreferenceFromHistoricalCalls(preferenceId);
    }

    @Transactional
    public void resetCallsForPreference(CallbackPreference pref) {
        log.info(
                "Resetting calls for preference={}",
                pref.getId());
        cancelFutureCallsForOnePreference(pref);
        ensureRollingCalls(pref);
    }

    @Transactional(readOnly = true)
    public ScheduledCall getCall(Long id) {
        log.info("Fetching scheduled call id={}", id);
        return scheduledCallRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Call not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ScheduledCall> getCallsForUser(Long userId) {

        log.info(
                "Fetching scheduled calls for user={}",
                userId);
        return scheduledCallRepository.findByUserId(userId);
    }

    @Transactional
    public void resetAllCallsForUser(Long userId) {

        log.info(
                "Resetting all future calls for user={}",
                userId);
        scheduledCallRepository.cancelFuturePendingCallsForUser(userId, Instant.now());

        List<CallbackPreference> prefs = callbackPreferenceRepository.findByUserId(userId);

        log.info(
                "Found {} preferences for user={}",
                prefs.size(),
                userId);

        for (CallbackPreference pref : prefs) {
            if (pref.getRepeat() == RepeatType.WEEKLY) {
                ensureRollingCalls(pref);
            }
        }
        log.info(
                "Finished resetting calls for user={}",
                userId);
    }
}
