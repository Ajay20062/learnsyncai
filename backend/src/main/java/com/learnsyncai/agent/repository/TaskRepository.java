package com.learnsyncai.agent.repository;

import com.learnsyncai.agent.model.Task;
import com.learnsyncai.agent.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStudyPlanIdOrderByDayNumberAsc(Long planId);
    List<Task> findByStudyPlanUserIdAndDayNumber(Long userId, Integer dayNumber);
    long countByStudyPlanUserIdAndDayNumberLessThanAndStatus(Long userId, Integer dayNumber, TaskStatus status);
    long countByStudyPlanUserId(Long userId);
    long countByStudyPlanUserIdAndStatus(Long userId, TaskStatus status);
}
