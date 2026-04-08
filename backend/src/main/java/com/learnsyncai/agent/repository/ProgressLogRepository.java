package com.learnsyncai.agent.repository;

import com.learnsyncai.agent.model.ProgressLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProgressLogRepository extends JpaRepository<ProgressLog, Long> {
    List<ProgressLog> findByUserIdOrderByLogDateDesc(Long userId);
    List<ProgressLog> findByUserIdAndLogDateBetween(Long userId, LocalDate start, LocalDate end);
    Optional<ProgressLog> findByUserIdAndTaskIdAndLogDate(Long userId, Long taskId, LocalDate date);
    long countByUserIdAndCompletedTrueAndLogDateBetween(Long userId, LocalDate start, LocalDate end);
}
