package com.learnsyncai.agent.service;

import com.learnsyncai.agent.dto.DashboardResponse;
import com.learnsyncai.agent.dto.ProgressUpdateRequest;
import com.learnsyncai.agent.dto.TaskDto;
import com.learnsyncai.agent.dto.WeeklyPointDto;
import com.learnsyncai.agent.exception.BadRequestException;
import com.learnsyncai.agent.exception.ResourceNotFoundException;
import com.learnsyncai.agent.model.ProgressLog;
import com.learnsyncai.agent.model.Task;
import com.learnsyncai.agent.model.TaskStatus;
import com.learnsyncai.agent.model.User;
import com.learnsyncai.agent.repository.ProgressLogRepository;
import com.learnsyncai.agent.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    private final TaskRepository taskRepository;
    private final ProgressLogRepository progressLogRepository;
    private final CurrentUserService currentUserService;

    public ProgressService(TaskRepository taskRepository,
                           ProgressLogRepository progressLogRepository,
                           CurrentUserService currentUserService) {
        this.taskRepository = taskRepository;
        this.progressLogRepository = progressLogRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public void updateProgress(ProgressUpdateRequest request) {
        User user = currentUserService.getCurrentUser();
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (!task.getStudyPlan().getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Task does not belong to current user");
        }
        task.setStatus(Boolean.TRUE.equals(request.getCompleted()) ? TaskStatus.COMPLETED : TaskStatus.PENDING);
        taskRepository.save(task);

        LocalDate today = LocalDate.now();
        ProgressLog log = progressLogRepository.findByUserIdAndTaskIdAndLogDate(user.getId(), task.getId(), today)
                .orElseGet(ProgressLog::new);
        log.setUser(user);
        log.setTask(task);
        log.setCompleted(request.getCompleted());
        log.setLogDate(today);
        progressLogRepository.save(log);
    }

    public DashboardResponse getDashboardData() {
        User user = currentUserService.getCurrentUser();
        long totalTasks = taskRepository.countByStudyPlanUserId(user.getId());
        long completedTasks = taskRepository.countByStudyPlanUserIdAndStatus(user.getId(), TaskStatus.COMPLETED);

        double completionPercent = totalTasks == 0 ? 0 : (completedTasks * 100.0 / totalTasks);
        int currentDay = resolveCurrentDayForUser(user.getId());
        int missedTasks = (int) taskRepository.countByStudyPlanUserIdAndDayNumberLessThanAndStatus(
                user.getId(),
                currentDay,
                TaskStatus.PENDING
        );

        List<TaskDto> todayTasks = taskRepository.findByStudyPlanUserIdAndDayNumber(user.getId(), currentDay)
                .stream()
                .map(task -> new TaskDto(task.getId(), task.getDayNumber(), task.getDescription(), task.getRevisionNote(), task.getStatus()))
                .toList();
        int todayTaskCount = todayTasks.size();
        int todayCompleted = (int) todayTasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();

        int streak = calculateStreak(user.getId());
        List<WeeklyPointDto> weeklyChart = buildWeeklySeries(user.getId());

        return new DashboardResponse(
                todayTaskCount,
                todayCompleted,
                streak,
                Math.round(completionPercent * 100.0) / 100.0,
                missedTasks,
                weeklyChart,
                todayTasks
        );
    }

    public double calculateCompletionPercent(Long userId) {
        long totalTasks = taskRepository.countByStudyPlanUserId(userId);
        long completedTasks = taskRepository.countByStudyPlanUserIdAndStatus(userId, TaskStatus.COMPLETED);
        return totalTasks == 0 ? 0.0 : (completedTasks * 100.0 / totalTasks);
    }

    private int resolveCurrentDayForUser(Long userId) {
        List<ProgressLog> logs = progressLogRepository.findByUserIdOrderByLogDateDesc(userId);
        if (logs.isEmpty()) {
            return 1;
        }
        int maxDayDone = logs.stream()
                .filter(ProgressLog::getCompleted)
                .map(log -> log.getTask().getDayNumber())
                .max(Integer::compareTo)
                .orElse(0);
        return Math.max(1, maxDayDone + 1);
    }

    private int calculateStreak(Long userId) {
        LocalDate day = LocalDate.now();
        int streak = 0;
        while (true) {
            LocalDate check = day.minusDays(streak);
            long count = progressLogRepository.countByUserIdAndCompletedTrueAndLogDateBetween(userId, check, check);
            if (count > 0) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    private List<WeeklyPointDto> buildWeeklySeries(Long userId) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);
        List<ProgressLog> logs = progressLogRepository.findByUserIdAndLogDateBetween(userId, start, end);
        Map<LocalDate, Long> countPerDate = logs.stream()
                .filter(ProgressLog::getCompleted)
                .collect(Collectors.groupingBy(ProgressLog::getLogDate, Collectors.counting()));

        List<WeeklyPointDto> points = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate current = start.plusDays(i);
            String dayLabel = current.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            points.add(new WeeklyPointDto(dayLabel, countPerDate.getOrDefault(current, 0L)));
        }
        return points;
    }
}
