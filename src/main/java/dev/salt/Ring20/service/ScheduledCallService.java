package dev.salt.Ring20.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import dev.salt.Ring20.entity.CallBackStatus;
import dev.salt.Ring20.entity.CallbackPreference;
import dev.salt.Ring20.entity.RepeatType;
import dev.salt.Ring20.entity.ScheduledCall;
import dev.salt.Ring20.repository.CallbackPreferenceRepository;
import dev.salt.Ring20.repository.ScheduledCallRepository;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduledCallService {
    private final CallbackPreferenceRepository callbackPreferenceRepository;
    private final ScheduledCallRepository scheduledCallRepository;
    private static final int WEEKLY_PENDING_CALLS = 4;

    public ScheduledCallService(
            CallbackPreferenceRepository callbackPreferenceRepository,
            ScheduledCallRepository scheduledCallRepository) {
        this.callbackPreferenceRepository = callbackPreferenceRepository;
        this.scheduledCallRepository = scheduledCallRepository;
    }

    @Transactional
    public void ensureRollingCalls(CallbackPreference pref) {

        if (pref.getRepeat() == RepeatType.NEVER) {
            long existing =
                    scheduledCallRepository.countFuturePendingCalls(
                            pref.getId(),
                            Instant.now());

            if (existing == 0) {
                scheduledCallRepository.save(
                        buildCall(pref, calculateNext(pref))
                );
            }

            return;
        }

        long existing =
                scheduledCallRepository.countFuturePendingCalls(
                        pref.getId(),
                        Instant.now());

        int toCreate = WEEKLY_PENDING_CALLS - (int) existing;

        if (toCreate <= 0) {
            return;
        }

        Instant nextTime = calculateNext(pref);

        while (toCreate > 0) {

            if (!alreadyExists(pref.getUser().getId(), nextTime)) {
                scheduledCallRepository.save(buildCall(pref, nextTime));
                toCreate--;
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
        return scheduledCallRepository.existsByUserIdAndTargetTime(userId, time);
    }

    private Instant calculateNext(CallbackPreference pref) {
        LocalDate today = LocalDate.now();
        LocalTime time = pref.getTime();

        DayOfWeek targetDay = DayOfWeek.valueOf(pref.getDay().name());

        LocalDate nextDate = today.with(TemporalAdjusters.nextOrSame(targetDay));

        if (nextDate.equals(today) && time.isBefore(LocalTime.now())) {
            nextDate = nextDate.plusWeeks(1);
        }

        return nextDate.atTime(time).atZone(ZoneId.systemDefault()).toInstant();
    }

    @Scheduled(fixedRate = 60000)
    public void handleScheduledCalls() {
        System.out.println("Scheduler running...");
        Instant now = Instant.now();

        List<ScheduledCall> startingNow =
                scheduledCallRepository.findCallsBetween(now.minusSeconds(30), now.plusSeconds(60));

        for (ScheduledCall call : startingNow) {

            if (call.getCallBackStatus() != CallBackStatus.PENDING) continue;

            if (call.getFcmToken() == null || call.getFcmToken().isBlank()) {
                continue;
            }

            boolean sent = sendNotification(call);

            if (sent) {
                call.setCallBackStatus(CallBackStatus.TRIGGERED);
                scheduledCallRepository.save(call);
            }
        }

        List<ScheduledCall> missedCalls =
                scheduledCallRepository.findAllMissedCalls(now);

        for (ScheduledCall call : missedCalls) {

            if (call.getCallBackStatus() == CallBackStatus.PENDING) {
                call.setCallBackStatus(CallBackStatus.MISSED);
                scheduledCallRepository.save(call);
            }
        }
        List<CallbackPreference> preferences =
                callbackPreferenceRepository.findAll();

        for (CallbackPreference pref : preferences) {
            if (pref.getRepeat() == RepeatType.WEEKLY) {
                ensureRollingCalls(pref);
            }
        }
    }

    public boolean sendNotification(ScheduledCall call) {
        try {
            if (call.getFcmToken() == null || call.getFcmToken().isBlank()) {
                return false;
            }

            Message message =
                    Message.builder()
                            .setToken(call.getFcmToken())
                            .putData("callId", String.valueOf(call.getId()))
                            .putData("trainerId", String.valueOf(call.getTrainerId()))
                            .putData("userId", String.valueOf(call.getUserId()))
                            .putData("type", "TRAINING_CALL")
                            .setNotification(
                                    Notification.builder()
                                            .setTitle("Time to train!")
                                            .setBody("Tap to start your session")
                                            .build())
                            .build();

            FirebaseMessaging.getInstance().send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public void completeCall(Long id) {
        ScheduledCall call =
                scheduledCallRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "No scheduled call exists with this id: " + id));
        if (call.getCallBackStatus() != CallBackStatus.TRIGGERED) {
            throw new IllegalStateException("Only triggered calls can be completed");
        }
        call.setCallBackStatus(CallBackStatus.COMPLETED);
        scheduledCallRepository.save(call);
    }

    @Transactional
    public void cancelCall(Long callId) {


        ScheduledCall call =
                scheduledCallRepository
                        .findById(callId)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Call not found with id: " + callId));

        if (call.getCallBackStatus() != CallBackStatus.PENDING) {
            throw new IllegalStateException("Only pending calls can be cancelled");
        }
        call.setCallBackStatus(CallBackStatus.CANCELLED);
        scheduledCallRepository.save(call);
    }
    @Transactional
    public void cancelFutureCallsForOnePreference(CallbackPreference pref) {
        scheduledCallRepository
                .cancelFuturePendingCallsForPreference(
                        pref.getId(),
                        Instant.now());
    }

    @Transactional
    public void detachHistoricalCallsFromPreference(Long preferenceId) {
        scheduledCallRepository.detachPreferenceFromHistoricalCalls( preferenceId);
    }

    @Transactional
    public void resetCallsForPreference(CallbackPreference pref) {
        cancelFutureCallsForOnePreference(pref);
        ensureRollingCalls(pref);
    }
}
