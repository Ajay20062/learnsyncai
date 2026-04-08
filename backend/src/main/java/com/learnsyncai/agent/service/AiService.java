package com.learnsyncai.agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.learnsyncai.agent.dto.GeneratedTaskItem;
import com.learnsyncai.agent.model.SkillLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private final WebClient webClient;

    @Value("${app.openai.api-url}")
    private String apiUrl;

    @Value("${app.openai.model}")
    private String model;

    @Value("${app.openai.api-key:}")
    private String apiKey;

    public AiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<GeneratedTaskItem> generateStudyPlan(String goal, int durationDays, SkillLevel level, int hoursPerDay) {
        String prompt = "Create a structured day-wise study plan for " + goal + " for " + durationDays +
                " days, considering " + level + " and " + hoursPerDay + " hours/day. Include revision.\n" +
                "Return strict JSON array format: " +
                "[{\"day\":1,\"task\":\"...\",\"revision\":\"...\"}]";
        String content = askLlm(prompt);
        List<GeneratedTaskItem> parsed = parseTasks(content, durationDays);
        if (!parsed.isEmpty()) {
            return parsed;
        }
        return buildFallbackPlan(goal, durationDays);
    }

    public String generateAdaptiveSuggestion(double completionPercent) {
        String prompt = "User completed only " + String.format("%.1f", completionPercent) +
                "%. Adjust next plan to improve consistency. Keep it below 80 words.";
        String response = askLlm(prompt);
        return response.isBlank()
                ? "Reduce load by 20%, focus on essentials, and add short daily revision blocks."
                : response.trim();
    }

    public String generateMotivationMessage() {
        String prompt = "Give a short motivational message for a student who missed tasks.";
        String response = askLlm(prompt);
        return response.isBlank()
                ? "Progress is built one focused session at a time. Start with a 25-minute sprint today."
                : response.trim();
    }

    private String askLlm(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return "";
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model);
        payload.put("temperature", 0.4);
        payload.put("messages", List.of(
                Map.of("role", "system", "content", "You are an expert study coach."),
                Map.of("role", "user", "content", prompt)
        ));

        try {
            JsonNode response = webClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            if (response == null) {
                return "";
            }
            return response.path("choices").path(0).path("message").path("content").asText("");
        } catch (Exception ex) {
            return "";
        }
    }

    private List<GeneratedTaskItem> parseTasks(String content, int maxDays) {
        List<GeneratedTaskItem> items = new ArrayList<>();
        if (content == null || content.isBlank()) {
            return items;
        }
        String cleaned = content.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replace("```json", "").replace("```", "").trim();
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            JsonNode arr = mapper.readTree(cleaned);
            if (!arr.isArray()) {
                return items;
            }
            for (JsonNode node : arr) {
                int day = node.path("day").asInt();
                if (day <= 0 || day > maxDays) {
                    continue;
                }
                String task = node.path("task").asText("");
                String revision = node.path("revision").asText("Quick recap and spaced repetition.");
                if (!task.isBlank()) {
                    items.add(new GeneratedTaskItem(day, task, revision));
                }
            }
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
        return items;
    }

    private List<GeneratedTaskItem> buildFallbackPlan(String goal, int durationDays) {
        List<GeneratedTaskItem> fallback = new ArrayList<>();
        for (int day = 1; day <= durationDays; day++) {
            String task = "Day " + day + ": Learn and practice core topics for " + goal + ".";
            String revision = "Review key notes and solve a short recall quiz.";
            fallback.add(new GeneratedTaskItem(day, task, revision));
        }
        return fallback;
    }
}
