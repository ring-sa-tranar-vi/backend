package dev.salt.Ring20.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import dev.salt.Ring20.entity.CallBackStatus;
import dev.salt.Ring20.entity.CallbackPreference;
import dev.salt.Ring20.entity.ScheduledCall;
import dev.salt.Ring20.repository.CallbackPreferenceRepository;
import dev.salt.Ring20.repository.ScheduledCallRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class ScheduledCallService {
    private final CallbackPreferenceRepository callbackPreferenceRepository;
    private final ScheduledCallRepository scheduledCallRepository;

    public ScheduledCallService(CallbackPreferenceRepository callbackPreferenceRepository, ScheduledCallRepository scheduledCallRepository) {
        this.callbackPreferenceRepository = callbackPreferenceRepository;
        this.scheduledCallRepository = scheduledCallRepository;
    }

    public void generateCallsFromPreferences() {
        List<CallbackPreference> prefs = callbackPreferenceRepository.findAll();

        for (CallbackPreference pref : prefs) {
            Instant nextCallTime = calculateNext(pref);

            ScheduledCall call = new ScheduledCall();
            call.setUserId(pref.getUser().getId());
            call.setTrainerId(1L); // or your logic
            call.setTargetTime(nextCallTime);
            call.setFcmToken(pref.getUser().getFcmToken());
            call.setCallBackStatus(CallBackStatus.PENDING);

            scheduledCallRepository.save(call);
        }
    }

    private Instant calculateNext(CallbackPreference pref) {
        LocalDate today = LocalDate.now();
        LocalTime time = pref.getTime();

        DayOfWeek targetDay = DayOfWeek.valueOf(pref.getDay().name());

        LocalDate nextDate = today.with(TemporalAdjusters.nextOrSame(targetDay));

        if (nextDate.equals(today) && time.isBefore(LocalTime.now())) {
            nextDate = nextDate.plusWeeks(1);
        }

        return nextDate.atTime(time)
                .atZone(ZoneId.systemDefault())
                .toInstant();
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

            call.setCallBackStatus(CallBackStatus.COMPLETED);
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

            Message message = Message.builder()
                    .setToken(call.getFcmToken())
                    .putData("trainerId", String.valueOf(call.getTrainerId()))
                    .putData("userId", String.valueOf(call.getUserId()))
                    .putData("type", "TRAINING_CALL")
                    .setNotification(Notification.builder()
                            .setTitle("Time to train!")
                            .setBody("Tap to start your session")
                            .build())
                    .build();

            FirebaseMessaging.getInstance().sendAsync(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
