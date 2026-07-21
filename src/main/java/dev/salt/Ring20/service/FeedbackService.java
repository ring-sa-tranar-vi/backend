package dev.salt.Ring20.service;

import dev.salt.Ring20.dto.AdminRecentFeedbackDto;
import dev.salt.Ring20.dto.AdminWorkoutFeedbackSummaryDto;
import dev.salt.Ring20.entity.*;
import dev.salt.Ring20.repository.ActivityLogRepository;
import dev.salt.Ring20.repository.FeedbackRepository;
import dev.salt.Ring20.repository.WorkoutRepository;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class FeedbackService {


    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String UNKNOWN_WORKOUT = "Unknown workout";
    private static final int MIN_FEEDBACK_FOR_REVIEW = 3;
    private static final double BAD_DISLIKE_RATE = 0.40;
    private static final double BAD_TOO_HARD_RATE = 0.50;
    private static final double BAD_MIN_RATING = 2.80;
    private static final double GOOD_MIN_RATING = 4.00;
    private static final double GOOD_MAX_DISLIKE_RATE = 0.20;
    private static final double GOOD_MAX_TOO_HARD_RATE = 0.30;
    private final FeedbackRepository feedbackRepository;
    private final WorkoutRepository workoutRepository;
    private final UserWorkoutPreferenceService preferenceService;
    private final ActivityLogRepository activityLogRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, WorkoutRepository workoutRepository, UserWorkoutPreferenceService preferenceService, ActivityLogRepository activityLogRepository) {
        this.feedbackRepository = feedbackRepository;
        this.workoutRepository = workoutRepository;
        this.preferenceService = preferenceService;
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public Feedback addFeedback(Feedback feedback) {
        validateFeedback(feedback);
        attachActivityLog(feedback);
        feedback.setCreatedAt(LocalDateTime.now());
        Feedback savedFeedback = feedbackRepository.save(feedback);

        if (Boolean.FALSE.equals(feedback.getLiked())) {
            preferenceService.addPreference(feedback.getUserId(), feedback.getWorkoutId(), UserWorkoutPreferenceType.DISLIKED);
        }

        if (Boolean.TRUE.equals(feedback.getLiked())) {
            preferenceService.removePreference(feedback.getUserId(), feedback.getWorkoutId(), UserWorkoutPreferenceType.DISLIKED);
        }

        return savedFeedback;
    }

    private void validateFeedback(Feedback feedback) {
        if (feedback.getUserId() == null || feedback.getWorkoutId() == null) {
            throw new IllegalArgumentException("UserId and workoutId are required.");
        }

        Integer rating = feedback.getRating();
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        if (feedback.getDifficulty() == null && rating == null && feedback.getLiked() == null) {
            throw new IllegalArgumentException("At least one of difficulty, liked, or rating must be provided.");
        }

        Long activityLogId = feedback.getActivityLogId();
        if (activityLogId != null) {
            ActivityLog activityLog = activityLogRepository.findById(activityLogId).orElseThrow(() -> new IllegalArgumentException("ActivityLogId does not exist"));

            if (!Objects.equals(activityLog.getUserId(), feedback.getUserId()) || !Objects.equals(activityLog.getWorkoutId(), feedback.getWorkoutId())) {
                throw new IllegalArgumentException("ActivityLogId must match userId and workoutId");
            }
        }
    }

    private void attachActivityLog(Feedback feedback) {
        if (feedback.getActivityLogId() != null) {
            return;
        }

        Long userId = feedback.getUserId();
        Long workoutId = feedback.getWorkoutId();

        Optional<Long> matchedActivityLogId = activityLogRepository.findTopByUserIdAndWorkoutIdAndStatusOrderByCompletedAtDesc(userId, workoutId, STATUS_COMPLETED).or(() -> activityLogRepository.findTopByUserIdAndWorkoutIdOrderByCompletedAtDesc(userId, workoutId)).map(ActivityLog::getId);

        if (matchedActivityLogId.isPresent()) {
            feedback.setActivityLogId(matchedActivityLogId.get());
            return;
        }

        ActivityLog fallbackLog = new ActivityLog();
        fallbackLog.setUserId(userId);
        fallbackLog.setWorkoutId(workoutId);
        fallbackLog.setStatus(STATUS_COMPLETED);
        fallbackLog.setCreatedAt(LocalDateTime.now());
        ActivityLog savedLog = activityLogRepository.save(fallbackLog);
        feedback.setActivityLogId(savedLog.getId());
    }

    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Feedback not found with id: " + id));
    }

    public List<Feedback> getFeedbackByUserId(Long userId) {
        return feedbackRepository.findByUserId(userId);
    }

    public List<Feedback> getFeedbackByWorkoutId(Long workoutId) {
        return feedbackRepository.findByWorkoutId(workoutId);
    }

    public List<Feedback> getFeedbackByUserAndWorkout(Long userId, Long workoutId) {
        return feedbackRepository.findByUserIdAndWorkoutId(userId, workoutId);
    }

    @Transactional
    public void deleteFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Feedback not found with id: " + id));

        feedbackRepository.delete(feedback);
    }

    public List<AdminWorkoutFeedbackSummaryDto> getWorkoutFeedbackSummary() {
        List<Workout> workouts = workoutRepository.findAll();

        Map<Long, List<Feedback>> feedbackByWorkoutId = feedbackRepository.findAll().stream().filter(feedback -> feedback.getWorkoutId() != null).collect(Collectors.groupingBy(Feedback::getWorkoutId));

        return workouts.stream().map(workout -> createWorkoutSummary(workout, feedbackByWorkoutId.getOrDefault(workout.getId(), List.of()))).toList();
    }

    private AdminWorkoutFeedbackSummaryDto createWorkoutSummary(Workout workout, List<Feedback> feedbacks) {
        int feedbackCount = feedbacks.size();

        long ratingCount = feedbacks.stream().filter(feedback -> feedback.getRating() != null).count();

        double ratingSum = feedbacks.stream().filter(feedback -> feedback.getRating() != null).mapToDouble(Feedback::getRating).sum();

        long dislikedCount = feedbacks.stream().filter(feedback -> Boolean.FALSE.equals(feedback.getLiked())).count();

        long tooHardCount = feedbacks.stream().filter(feedback -> feedback.getDifficulty() == FeedbackDifficulty.TOO_HARD).count();

        double avgRating = calculateRate(ratingSum, ratingCount);
        double dislikeRate = calculateRate(dislikedCount, feedbackCount);
        double tooHardRate = calculateRate(tooHardCount, feedbackCount);

        return new AdminWorkoutFeedbackSummaryDto(workout.getId(), workout.getName(), feedbackCount, avgRating, dislikeRate, tooHardRate, deriveStatus(feedbackCount, avgRating, dislikeRate, tooHardRate));
    }

    private double calculateRate(double numerator, double denominator) {
        return denominator == 0 ? 0 : roundTwoDecimals(numerator / denominator);
    }

    public List<AdminRecentFeedbackDto> getRecentFeedbackEntries() {
        List<Feedback> feedbacks = new ArrayList<>(feedbackRepository.findAll());
        feedbacks.sort(Comparator.comparing(Feedback::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())));

        List<AdminRecentFeedbackDto> result = new ArrayList<>();
        for (Feedback feedback : feedbacks) {
            String workoutName = workoutRepository.findById(feedback.getWorkoutId()).map(Workout::getName).orElse(UNKNOWN_WORKOUT);

            result.add(new AdminRecentFeedbackDto(feedback.getId(), feedback.getUserId(), feedback.getWorkoutId(), feedback.getActivityLogId(), workoutName, feedback.getDifficulty(), feedback.getLiked(), feedback.getRating(), feedback.getComment(), feedback.getCreatedAt()));
        }

        return result;
    }

    private String deriveStatus(int feedbackCount, double avgRating, double dislikeRate, double tooHardRate) {
        if (feedbackCount < MIN_FEEDBACK_FOR_REVIEW) {
            return FeedbackStatus.NEEDS_REVIEW.toString();
        }

        if (isBadFeedback(avgRating, dislikeRate, tooHardRate)) {
            return FeedbackStatus.BAD.toString();
        }

        if (isGoodFeedback(avgRating, dislikeRate, tooHardRate)) {
            return FeedbackStatus.GOOD.toString();
        }

        return FeedbackStatus.NEEDS_REVIEW.toString();
    }

    private boolean isBadFeedback(double avgRating, double dislikeRate, double tooHardRate) {
        return dislikeRate >= BAD_DISLIKE_RATE || tooHardRate >= BAD_TOO_HARD_RATE || (avgRating > 0 && avgRating < BAD_MIN_RATING);
    }

    private boolean isGoodFeedback(double avgRating, double dislikeRate, double tooHardRate) {
        return avgRating >= GOOD_MIN_RATING && dislikeRate <= GOOD_MAX_DISLIKE_RATE && tooHardRate <= GOOD_MAX_TOO_HARD_RATE;
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
