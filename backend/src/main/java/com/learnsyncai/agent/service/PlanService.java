package com.learnsyncai.agent.service;

import com.learnsyncai.agent.dto.*;
import com.learnsyncai.agent.exception.ResourceNotFoundException;
import com.learnsyncai.agent.model.StudyPlan;
import com.learnsyncai.agent.model.Task;
import com.learnsyncai.agent.model.TaskStatus;
import com.learnsyncai.agent.model.User;
import com.learnsyncai.agent.repository.StudyPlanRepository;
import com.learnsyncai.agent.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final TaskRepository taskRepository;
    private final CurrentUserService currentUserService;
    private final AiService aiService;
    private final ProgressService progressService;

    public PlanService(StudyPlanRepository studyPlanRepository,
                       TaskRepository taskRepository,
                       CurrentUserService currentUserService,
                       AiService aiService,
                       ProgressService progressService) {
        this.studyPlanRepository = studyPlanRepository;
        this.taskRepository = taskRepository;
        this.currentUserService = currentUserService;
        this.aiService = aiService;
        this.progressService = progressService;
    }

    @Transactional
    public StudyPlanResponse generatePlan(StudyPlanRequest request) {
        User user = currentUserService.getCurrentUser();

        StudyPlan plan = new StudyPlan();
        plan.setUser(user);
        plan.setGoal(request.getGoal());
        plan.setDurationDays(request.getDurationDays());
        plan.setDifficulty(request.getSkillLevel());
        plan.setDailyHours(request.getDailyHours());
        StudyPlan savedPlan = studyPlanRepository.save(plan);

        List<GeneratedTaskItem> generated = aiService.generateStudyPlan(
                request.getGoal(),
                request.getDurationDays(),
                request.getSkillLevel(),
                request.getDailyHours()
        );
        List<GeneratedTaskItem> normalizedTasks = normalizeGeneratedTasks(
                generated,
                request.getGoal(),
                request.getDurationDays()
        );

        for (GeneratedTaskItem item : normalizedTasks) {
            Task task = new Task();
            task.setStudyPlan(savedPlan);
            task.setDayNumber(item.getDayNumber());
            task.setDescription(item.getTask());
            task.setRevisionNote(item.getRevision());
            task.setStatus(TaskStatus.PENDING);
            savedPlan.getTasks().add(task);
        }
        StudyPlan finalPlan = studyPlanRepository.save(savedPlan);
        return mapPlan(finalPlan);
    }

    public StudyPlanResponse getLatestPlan() {
        User user = currentUserService.getCurrentUser();
        StudyPlan plan = studyPlanRepository.findTopByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new ResourceNotFoundException("No study plan found"));
        return mapPlan(plan);
    }

    public AdaptivePlanResponse getAdaptiveRecommendation() {
        User user = currentUserService.getCurrentUser();
        double completionPercent = progressService.calculateCompletionPercent(user.getId());
        String recommendation = aiService.generateAdaptiveSuggestion(completionPercent);
        return new AdaptivePlanResponse(completionPercent, recommendation);
    }

    private List<GeneratedTaskItem> normalizeGeneratedTasks(List<GeneratedTaskItem> rawTasks, String goal, int durationDays) {
        Map<Integer, GeneratedTaskItem> byDay = new HashMap<>();
        for (GeneratedTaskItem item : rawTasks) {
            int day = item.getDayNumber();
            if (day <= 0 || day > durationDays || byDay.containsKey(day)) {
                continue;
            }
            byDay.put(day, item);
        }

        List<GeneratedTaskItem> normalized = new ArrayList<>();
        for (int day = 1; day <= durationDays; day++) {
            GeneratedTaskItem existing = byDay.get(day);
            if (existing != null) {
                normalized.add(existing);
                continue;
            }
            normalized.add(new GeneratedTaskItem(
                    day,
                    "Day " + day + ": Continue focused practice for " + goal + ".",
                    "Review notes and do a short recall exercise."
            ));
        }
        normalized.sort(Comparator.comparingInt(GeneratedTaskItem::getDayNumber));
        return normalized;
    }

    private StudyPlanResponse mapPlan(StudyPlan plan) {
        List<TaskDto> taskDtos = taskRepository.findByStudyPlanIdOrderByDayNumberAsc(plan.getId())
                .stream()
                .map(task -> new TaskDto(
                        task.getId(),
                        task.getDayNumber(),
                        task.getDescription(),
                        task.getRevisionNote(),
                        task.getStatus()
                ))
                .toList();

        return new StudyPlanResponse(
                plan.getId(),
                plan.getGoal(),
                plan.getDurationDays(),
                plan.getDailyHours(),
                plan.getDifficulty(),
                taskDtos
        );
    }
}
