package dev.salt.Ring20.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.salt.Ring20.entity.User;
import dev.salt.Ring20.entity.Workout;
import dev.salt.Ring20.exception.QuotaExceededException;
import dev.salt.Ring20.exception.WorkoutRecommendationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiWorkoutService {

    private final String googleApiKey;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(GeminiWorkoutService.class);

    // gemini-3.5-flash-lite pricing (USD per 1M tokens)
    private static final double INPUT_PRICE_USD_PER_MILLION_TOKENS = 0.30;
    private static final double OUTPUT_PRICE_USD_PER_MILLION_TOKENS = 2.50;

    private static final String PARTS_KEY = "parts";

    public GeminiWorkoutService(
            @Value("${gemini.api-key:}") String googleApiKey,
            ObjectMapper objectMapper,
            RestTemplate restTemplate) {
        this.googleApiKey = googleApiKey;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        log.debug("Swapped to production REST optimization engine.");
    }

    public CompletableFuture<String> recommendWorkoutWithReasoning(
            User user, List<Workout> workouts) {
        if (googleApiKey.isBlank()) {
            throw new IllegalStateException("Gemini API key is missing");
        }
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        List<Map<String, Object>> workoutsForPrompt =
                                workouts.stream()
                                        .map(
                                                w -> {
                                                    Map<String, Object> m = new HashMap<>();
                                                    m.put("id", w.getId());
                                                    m.put("name", w.getName());
                                                    m.put("level", w.getLevel());
                                                    m.put("type", w.getType());
                                                    m.put("description", w.getDescription());
                                                    return m;
                                                })
                                        .toList();

                        String workoutsListJson =
                                objectMapper.writeValueAsString(workoutsForPrompt);

                        String userIntensity =
                                user.getIntensityLevel() == null
                                        ? "unspecified"
                                        : user.getIntensityLevel().toString();
                        String userContext = user.getContext() == null ? "" : user.getContext();

                        String promptTemplate =
                                """
                                User Profile Data:
                                - Name: %s
                                - Intensity Level (reference): %s
                                - Context / Preferences: %s

                                Available Workout Options (JSON array):
                                %s

                                Pick exactly one workout that best matches the user's profile, the reasoning behind the decision, and return a strict JSON object structure matching: {"workoutId": 1, "reasoning": "string text"}. Disregard the users preferred language and the workouts are written in when making your decision""";
                        String promptData =
                                String.format(
                                        promptTemplate,
                                        user.getName(),
                                        userIntensity,
                                        userContext,
                                        workoutsListJson);

                        Map<String, Object> textPart = Map.of("text", promptData);
                        Map<String, Object> partsBlock = Map.of(PARTS_KEY, List.of(textPart));
                        Map<String, Object> contentsBlock =
                                Map.of("role", "user", PARTS_KEY, partsBlock.get(PARTS_KEY));

                        Map<String, Object> generationConfig =
                                Map.of("responseMimeType", "application/json");

                        Map<String, Object> requestBody =
                                Map.of(
                                        "contents",
                                        List.of(contentsBlock),
                                        "generationConfig",
                                        generationConfig);

                        String url =
                                "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent?key="
                                        + googleApiKey;
                        String rawResponse =
                                restTemplate.postForObject(url, requestBody, String.class);

                        JsonNode root = objectMapper.readTree(rawResponse);
                        String resultJsonString =
                                root.path("candidates")
                                        .path(0)
                                        .path("content")
                                        .path(PARTS_KEY)
                                        .path(0)
                                        .path("text")
                                        .asText();

                        JsonNode usage = root.path("usageMetadata");
                        int promptTokens = usage.path("promptTokenCount").asInt(0);
                        int responseTokens = usage.path("candidatesTokenCount").asInt(0);
                        int totalTokens = usage.path("totalTokenCount").asInt(0);
                        double estimatedCostUsd =
                                (promptTokens * INPUT_PRICE_USD_PER_MILLION_TOKENS
                                                + responseTokens
                                                        * OUTPUT_PRICE_USD_PER_MILLION_TOKENS)
                                        / 1_000_000;
                        log.info(
                                "Gemini token usage - prompt: {}, response: {}, total: {}, estimated cost: ${}",
                                promptTokens,
                                responseTokens,
                                totalTokens,
                                String.format("%.6f", estimatedCostUsd));

                        log.debug("Gemini response captured: {}", resultJsonString);
                        return resultJsonString.trim();

                    } catch (HttpClientErrorException.TooManyRequests e) {
                        throw new QuotaExceededException(
                                "API quota exceeded. Please try again later.", e);

                    } catch (Exception e) {
                        throw new WorkoutRecommendationException(
                                "Failed to generate workout recommendation", e);
                    }
                });
    }
}
