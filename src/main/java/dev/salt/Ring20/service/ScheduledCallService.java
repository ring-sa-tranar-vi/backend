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

@Service
public class ScheduledCallService {
    private final CallbackPreferenceRepository callbackPreferenceRepository;
    private final ScheduledCallRepository scheduledCallRepository;
    private static final int REPEAT_TIMES_IF_WEEKLY = 4;
    private static final int NO_REPEAT_TIMES = 1;

    public ScheduledCallService(
            CallbackPreferenceRepository callbackPreferenceRepository,
            ScheduledCallRepository scheduledCallRepository) {
        this.callbackPreferenceRepository = callbackPreferenceRepository;
        this.scheduledCallRepository = scheduledCallRepository;
    }

    public void cancelCall(Long callId) {
        ScheduledCall call =
                scheduledCallRepository
                        .findById(callId)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Call not found with id: " + callId));

        call.setCallBackStatus(CallBackStatus.CANCELLED);
        scheduledCallRepository.save(call);
    }

    public void resetCallsForPreference(CallbackPreference pref) {
        scheduledCallRepository.deleteByUserIdAndDayAndTargetTimeAfterAndCallBackStatus(
                pref.getUser().getId(), pref.getDay(), Instant.now(), CallBackStatus.PENDING);
    }

    public void generateCallsForPreference(CallbackPreference pref) {

        int occurrences = getOccurrences(pref);

        for (int i = 0; i < occurrences; i++) {

            Instant targetTime = getTargetTime(pref, i);

            if (alreadyExists(pref.getUser().getId(), targetTime)) continue;

            scheduledCallRepository.save(buildCall(pref, targetTime));
        }
    }

    private ScheduledCall buildCall(CallbackPreference pref, Instant time) {
        ScheduledCall call = new ScheduledCall();
        call.setUserId(pref.getUser().getId());
        call.setTrainerId(pref.getUser().getTrainerId());
        call.setTargetTime(time);
        call.setFcmToken(pref.getUser().getFcmToken());
        call.setCallBackStatus(CallBackStatus.PENDING);
        return call;
    }

    private boolean alreadyExists(Long userId, Instant time) {
        return scheduledCallRepository.existsByUserIdAndTargetTime(userId, time);
    }

    private Instant getTargetTime(CallbackPreference pref, int weekOffset) {
        return calculateNext(pref).plus(weekOffset, ChronoUnit.WEEKS);
    }

    private int getOccurrences(CallbackPreference pref) {
        return pref.getRepeat() == RepeatType.WEEKLY ? REPEAT_TIMES_IF_WEEKLY : NO_REPEAT_TIMES;
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
        Instant now = Instant.now();

        List<ScheduledCall> startingNow =
                scheduledCallRepository.findCallsBetween(now, now.plusSeconds(60));

        for (ScheduledCall call : startingNow) {

            if (call.getCallBackStatus() != CallBackStatus.PENDING) continue;

            if (call.getFcmToken() == null || call.getFcmToken().isBlank()) {
                continue;
            }

            sendNotification(call);

            call.setCallBackStatus(CallBackStatus.TRIGGERED);
            scheduledCallRepository.save(call);
        }

        List<ScheduledCall> missedCalls =
                scheduledCallRepository.findMissedCalls(now.minusSeconds(60));

        for (ScheduledCall call : missedCalls) {

            if (call.getCallBackStatus() == CallBackStatus.PENDING) {
                call.setCallBackStatus(CallBackStatus.MISSED);
                scheduledCallRepository.save(call);
            }
        }
    }

    public void sendNotification(ScheduledCall call) {
        try {
            if (call.getFcmToken() == null || call.getFcmToken().isBlank()) {
                return;
            }

            Message message =
                    Message.builder()
                            .setToken(call.getFcmToken())
                            .putData("trainerId", String.valueOf(call.getTrainerId()))
                            .putData("userId", String.valueOf(call.getUserId()))
                            .putData("type", "TRAINING_CALL")
                            .setNotification(
                                    Notification.builder()
                                            .setTitle("Time to train!")
                                            .setBody("Tap to start your session")
                                            .build())
                            .build();

            FirebaseMessaging.getInstance().sendAsync(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void completeCall(Long id) {
        ScheduledCall call =
                scheduledCallRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "No scheduled call exist with this id: " + id));
        call.setCallBackStatus(CallBackStatus.COMPLETED);
        scheduledCallRepository.save(call);
    }

    public void resetCallsForUser(Long userId) {
        scheduledCallRepository.deleteByUserIdAndTargetTimeAfterAndCallBackStatus(userId, Instant.now(), CallBackStatus.PENDING);
        List<CallbackPreference> prefs = callbackPreferenceRepository.findByUserId(userId);
        for (CallbackPreference pref : prefs){
            generateCallsForPreference(pref);
        }
    }
}
