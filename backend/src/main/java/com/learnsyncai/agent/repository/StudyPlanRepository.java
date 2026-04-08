package com.learnsyncai.agent.repository;

import com.learnsyncai.agent.model.StudyPlan;
import com.learnsyncai.agent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {
    List<StudyPlan> findByUserOrderByCreatedAtDesc(User user);
    Optional<StudyPlan> findTopByUserOrderByCreatedAtDesc(User user);
}
