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

        // convert to next correct weekday
        DayOfWeek targetDay = DayOfWeek.valueOf(pref.getDay().name());

        LocalDate nextDate = today.with(TemporalAdjusters.nextOrSame(targetDay));

        // if today but time already passed → next week
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

        // 1. Calls starting NOW (0–1 min)
        List<ScheduledCall> startingNow =
                scheduledCallRepository.findCallsBetween(now, now.plusSeconds(60));

        for (ScheduledCall call : startingNow) {
            sendNotification(call);
        }

        // 2. Calls in 3–5 min (AI prewarm)
        List<ScheduledCall> upcoming =
                scheduledCallRepository.findCallsBetween(now.plusSeconds(180), now.plusSeconds(300));
    }

    // ✅ Send FCM
    public void sendNotification(ScheduledCall call) {
        try {
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
